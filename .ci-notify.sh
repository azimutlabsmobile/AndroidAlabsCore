#!/bin/bash

TIME="10"
URL="https://api.telegram.org/bot1151581419:AAEkje0OH96qQA9tkIOL_iH6pigqGguFfNk/sendMessage"
TEXT="Deploy status: $1%0A%0AProject:+$CI_PROJECT_NAME%0AURL:+$CI_PROJECT_URL/pipelines/$CI_PIPELINE_ID/%0ABranch:+$CI_COMMIT_REF_SLUG%0AUser:+$GITLAB_USER_EMAIL%0A
Reviewers : @a_k_y_l @yergali_zhakhan"

for i in -364514300 -455083578
do
echo $i
  curl -s --max-time $TIME -d "chat_id=$i&disable_web_page_preview=1&text=$TEXT" $URL > /dev/null
done