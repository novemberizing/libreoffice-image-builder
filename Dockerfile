FROM openjdk:8
RUN apt-get update
RUN apt -y install libreoffice libreoffice-calc libreoffice-common libreoffice-core libreoffice-dev libreoffice-dev-common libreoffice-draw libreoffice-dmaths libreoffice-impress libreoffice-java-common libreoffice-math libreoffice-style-breeze libreoffice-style-colibre libreoffice-style-sifr libreoffice-style-tango libreoffice-writer uno-libs3 ure unoconv
RUN apt search libreoffice
COPY . /usr/src/libreoffice
WORKDIR /usr/src/libreoffice
RUN ./gradlew build
