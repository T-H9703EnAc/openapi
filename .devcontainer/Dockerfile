# ベースイメージ:Ubuntu 24:04
FROM ubuntu:24.04

# パッケージのインストール時に対話的なプロンプトが表示されないように設定する
ENV DEBIAN_FRONTEND=noninteractive

# 必要なパッケージのインストール
RUN set -eux && \ 
    apt-get update && \
    apt-get install -y \
    # bashコマンド補完
    bash-completion \
    # treeコマンド
    tree \
    # 基本的な開発ツール
    build-essential \  
    # URLからデータを転送するためのツール
    curl \
    # バージョン管理システム
    git \
    # テキストエディタ  
    vim \
    # 圧縮ツール
    zip \  
    # 解凍ツール
    unzip \
    # 圧縮アーカイブツール
    tar && \  
    # APTキャッシュのクリーンアップ
    apt-get clean && \
    # APTリポジトリのメタデータを削除してディスクスペースを節約
    rm -rf /var/lib/apt/lists/*



# AdoptiumのGitプロジェクトのベースURL
ARG BASE_URL_JDK=https://github.com/adoptium
# JDK 17のバイナリがあるプロジェクト
ARG JDK_DOWNLOAD=temurin17-binaries/releases/download
# JDK 17のリリースバージョン
ARG JDK_RELEASE=jdk-17.0.11%2B9
# JDKのバージョン番号
ARG JDK_VERSION=17.0.11_9
# JDKのファイル名
ARG JDK_FILE_NAME=OpenJDK17U-jdk_x64_linux_hotspot_${JDK_VERSION}.tar.gz

# OpenJDKのダウンロードとインストール
RUN set -eux && \
    # OpenJDKを指定されたURLからダウンロードし、一時ディレクトリに保存
    curl -L -o /tmp/openjdk.tar.gz ${BASE_URL_JDK}/${JDK_DOWNLOAD}/${JDK_RELEASE}/${JDK_FILE_NAME} && \
    # インストール先のディレクトリを作成
    mkdir -p /usr/lib/jvm/java-17-openjdk-amd64 && \
    # ダウンロードしたtar.gzファイルを解凍し、指定ディレクトリに展開
    tar -xzf /tmp/openjdk.tar.gz -C /usr/lib/jvm/java-17-openjdk-amd64 --strip-components 1 && \
    # 解凍後、一時ファイルを削除
    rm /tmp/openjdk.tar.gz

# 環境変数の設定
ENV JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64
ENV PATH="${JAVA_HOME}/bin:${PATH}"



# Kotlinコンパイラのダウンロードとインストール
ARG BASE_URL_KOTLIN=https://github.com/JetBrains/kotlin/releases/download/
# Kotlinコンパイラのバージョン
ARG KOTLIN_VERSION=2.0.0
# Kotlinコンパイラのファイル名
ARG KOTLIN_FILE_NAME=kotlin-compiler-${KOTLIN_VERSION}.zip

# Kotlinのダウンロード
RUN set -eux && \
    # Kotlinコンパイラを指定されたURLからダウンロードし、一時ディレクトリに保存
    curl -L -o /tmp/kotlin-compiler.zip ${BASE_URL_KOTLIN}/v${KOTLIN_VERSION}/kotlin-compiler-${KOTLIN_VERSION}.zip && \
    # ダウンロードしたZIPファイルを解凍し、指定ディレクトリに展開
    unzip /tmp/kotlin-compiler.zip -d /opt/kotlin && \
    # 解凍後、一時ファイルを削除
    rm /tmp/kotlin-compiler.zip

# 環境変数の設定
ENV KOTLIN_HOME=/opt/kotlin/kotlin-compiler-${KOTLIN_VERSION}
ENV PATH="${KOTLIN_HOME}/bin:${PATH}"

WORKDIR /app