FROM ubuntu:latest
LABEL authors="Konrad"

ENTRYPOINT ["top", "-b"]