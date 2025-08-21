#!/bin/bash

echo "배포 시작..."

# .env 파일 확인
if [ ! -f .env ]; then
    echo "ERROR: .env 파일이 없습니다."
    echo "nano .env 명령어로 환경변수를 설정하세요."
    exit 1
fi

# 기존 컨테이너 정리 및 코드 업데이트
docker-compose down
git pull origin main

# 빌드 및 실행
docker-compose up --build -d

echo "배포 완료!"
echo "상태 확인: docker-compose ps"
echo "로그 확인: docker-compose logs -f app"