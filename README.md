__NOVEMBERIZING'S IMAGE BUILDER USING LIBREOFFICE__
===================================================




## BUILD

```
gradle
```

## DOCKER

```
docker build -t novemberizing/orientalism-java .
docker push novemberizing/orientalism-java
```

## USAGE

```
./gradlew run --args='"[source]" "[destination]"'
```

https://www.baeldung.com/gradle-run-java-main
https://stackoverflow.com/questions/27604283/gradle-task-pass-arguments-to-java-application

docker run -it --rm libreoffice-image-builder ./gradlew run --args='"論語" "學而" "學而時習之" "子曰" "學而時習之 不亦說乎\n有朋自遠方來 不亦樂乎\n人不知而不慍 不亦君子乎" "/home/novemberizing/hello.png"'

-v /usr/lib/libreoffice/program:/usr/lib/libreoffice/program

docker run -it --rm libreoffice-image-builder ./gradlew run --args='"論語" "學而" "學而時習之" "子曰" "學而時習之 不亦說乎\n有朋自遠方來 不亦樂乎\n人不知而不慍 不亦君子乎" "/hello.png"'

```
docker run -it --rm -v ${PWD}/output:/output novemberizing/orientalism-java ./gradlew run --args='"論語" "學而" "學 而時習之" "子曰" "學而時習之 不亦說乎\n有朋自遠方來 不亦樂乎\n人不知而不慍 不亦君子乎" "/output/hello.png"'
```

- [x] 볼륨 마운트 아웃풋 파일
폰트 설치 한문
 
