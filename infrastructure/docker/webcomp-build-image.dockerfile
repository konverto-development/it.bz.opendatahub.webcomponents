FROM node:14.15

ARG JENKINS_GROUP_ID=2000
ARG JENKINS_USER_ID=2000
ARG SSH_CDN_ADDR
ARG SSH_CDN_USER

RUN groupadd --gid $JENKINS_GROUP_ID jenkins && \
    useradd --uid $JENKINS_USER_ID --gid $JENKINS_GROUP_ID --create-home jenkins

RUN apt-get update \
    && apt-get -y upgrade \
    && apt-get -y install --no-install-recommends \
        postgresql-client \
        jq \
        git \
        wget \
        uuid-runtime \
        ca-certificates \
        openssh-client \
    && apt-get clean \
    && rm -rf /var/lib/apt/lists/*

WORKDIR /work
COPY infrastructure/utils/wcstorecli.sh /work/wcstorecli.sh
COPY .env /work/.env

RUN . /work/.env
                    
RUN . /work/.env \
    && mkdir -p /work/.ssh  \
    && ssh-keyscan -H $SSH_CDN_ADDR >> /work/.ssh/known_hosts \
    && ssh-keyscan -H github.com >> /work/.ssh/known_hosts \
    && echo 'Host tomcattest2' >> /work/.ssh/config \
    && echo '  User $SSH_CDN_USER' >> /work/.ssh/config \
    && echo '  Hostname $SSH_CDN_ADDR' >> /work/.ssh/config 

RUN git config --global user.email "info@opendatahub.bz.it" \
    && git config --global user.name "Jenkins" \
 	&& git remote set-url origin $GIT_URL

