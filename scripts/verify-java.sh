#!/usr/bin/env bash
# examples/java 의 단일 파일 예제를 모두 실행한다. 하나라도 실패하면 종료 코드가 0이 아니다.
set -euo pipefail
cd "$(dirname "$0")/../examples/java"
java -version 2>&1 | head -1
for file in Ch*.java; do
  echo "== $file"
  java -Dstdout.encoding=UTF-8 "$file"
done
