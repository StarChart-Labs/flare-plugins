#!/bin/bash
urlencode() {
    # urlencode <string>
    old_lc_collate=$LC_COLLATE
    LC_COLLATE=C
    
    local length="${#1}"
    for (( i = 0; i < length; i++ )); do
        local c="${1:i:1}"
        case $c in
            [a-zA-Z0-9~_-]) printf "$c" ;;
            *) printf '%%%02X' "'$c" ;;
        esac
    done
    
    LC_COLLATE=$old_lc_collate
}

# Tests that the action workflow used is supported
if [ "$GITHUB_EVENT_NAME" = "pull_request" ]; then
    GITHUB_PR_ACTION="$(jq -r .action $GITHUB_EVENT_PATH)"
fi

if [ "$GITHUB_EVENT_NAME" = "push" ] || [ "$GITHUB_EVENT_NAME" = "pull_request" ]; then
    echo "Dependency scanning only support for push and pull_request events, skipping"
    
    exit 0
elif [ "$GITHUB_EVENT_NAME" = "pull_request" ] && [ "$GITHUB_PR_ACTION" != "opened" ] && [ "$GITHUB_PR_ACTION" != "reopened" ] && [ "$GITHUB_PR_ACTION" != "edited" ] && [ "$GITHUB_PR_ACTION" != "synchronize" ]; then
    echo "Dependency scanning only support for push and pull_request events, skipping"
    
    exit 0
fi

# Set variables conditional on action flow
if [ "$GITHUB_EVENT_NAME" = "pull_request" ]; then
    PR_BRANCH="$(jq -r .pullRequest.base.ref $GITHUB_EVENT_PATH)"
    PR_NUMBER="$(jq -r .number $GITHUB_EVENT_PATH)"
    
    COPILOT_BRANCH=$(urlencode $PR_BRANCH)
    COPILOT_PULL_REQUEST=$(urlencode $PR_NUMBER)
else
    PUSH_BRANCH=${GITHUB_REF:10}
    
    COPILOT_BRANCH=$(urlencode $PUSH_BRANCH)
    COPILOT_PULL_REQUEST=false
fi

echo "BRANCH: " + $COPILOT_BRANCH
echo "PR: " + $COPILOT_PULL_REQUEST


#Setup standardized environment
#GITHUB_ environment variables used here are documented at https://help.github.com/en/articles/virtual-environments-for-github-actions#environment-variables

COPILOT_DETECT_VERSION=${COPILOT_DETECT_VERSION:-5.6.2}
COPILOT_DETECT_OPTIONS=${COPILOT_DETECT_OPTIONS:-}

COPILOT_PROVIDER=${COPILOT_PROVIDER:-github}
COPILOT_REPO_SLUG=$(urlencode $GITHUB_REPOSITORY)

# TODO Temporarily testing with supported header, will need to determine data to use
COPILOT_BUILD_DATA=azure:1

COPILOT_URL="https://copilot.blackducksoftware.com/import?provider=$COPILOT_PROVIDER&repository=$COPILOT_REPO_SLUG&branch=$COPILOT_BRANCH&pull_request=$COPILOT_PULL_REQUEST"
COPILOT_REPORT_LOCATION=./.copilot/copilot_bom_bdio.jsonld

#Generate report with Hub Detect
echo "Generating CoPilot report"

export DETECT_LATEST_RELEASE_VERSION=$COPILOT_DETECT_VERSION
bash <(curl -s https://detect.synopsys.com/detect.sh) --detect.output.path=.copilot --detect.bom.aggregate.name=copilot_bom_bdio --detect.tools=DETECTOR,DOCKER --blackduck.offline.mode=true --detect.bdio.output.path=.copilot $COPILOT_DETECT_OPTIONS

if [ ! -f "$COPILOT_REPORT_LOCATION" ]; then
    echo "Report not generated at $COPILOT_REPORT_LOCATION"
    exit 1
fi

#Log that the script download is complete and proceeding
echo "Uploading report at $1"

#Log the curl version used
curl --version

curl -g -v -f -X POST -d @$COPILOT_REPORT_LOCATION -H 'Content-Type:application/ld+json' -H "CoPilot-Build-Data:$COPILOT_BUILD_DATA" "$COPILOT_URL"

#Exit with the curl command's output status
exit $?
