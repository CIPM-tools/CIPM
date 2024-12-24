#! /usr/bin/env zsh
#
alias evalFiles="fd --glob --full-path '**/*-*/*.json'"
alias historyFiles="fd --glob --full-path '**/commitHistoryEvaluationData.json'"
alias evalJson="evalFiles | xargs jq ."
alias historyJson="historyFiles | xargs jq ."
alias jqIndex="jq -s 'to_entries | .[] | {commitIndex:(.key+1)} + .value'"
alias jqIndexList="jq '.[]' | jqIndex | jq -s"

historyJson | jq '.commitPropagationEvals[] .imUpdateEval' | jqIndex | jq -s > imUpdateEval.json


# IM update
cat imUpdateEval.json | jq '.[] | {commitIndex, numberSIP, numberMatchedSIP, fScoreServiceInstrumentationPoints }' | jq -s > imUpdateEval-sip.json

cat imUpdateEval.json | jq '.[] | {commitIndex, numberAIP, numberMatchedAIP, fScoreActionInstrumentationPoints }' | jq -s > imUpdateEval-aip.json

cat imUpdateEval.json \
	| jq '.[] | {commitIndex, numberAIP, numberActiveAIP, numberMatchedActiveAIP, numberAddedActions, numberChangedActions, ratioActiveAIPs: (.ratioActiveAIPs*10000|round/10000), fScoreActiveActionInstrumentationPoints: (.fScoreActiveActionInstrumentationPoints*10000|round/10000) }' \
	| jq -s > imUpdateEval-active-aip.json

# PCM Update
historyJson | jq '.commitPropagationEvals [] .pcmUpdateEvals [0]' | jqIndex | jq -s > pcmUpdateEvalAutomatic.json

# Exec times
historyJson \
	| jq '.commitPropagationEvals[] | { execTime: .executionTimes.changePropagationTime, msPerChange: (.executionTimes.changePropagationTime / .changeStatistic.numberVitruvChanges*1000 | round / 1000), vitruvChanges: .changeStatistic.numberVitruvChanges  } + .changeStatistic.numberVitruvChangesPerModel' \
	| jqIndex | jq -s > execTimes.json

