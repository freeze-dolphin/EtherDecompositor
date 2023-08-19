all: clean build upload

build:
	gradle build

upload:
	printf "put build/libs/EtherDecompositor-0.1.0.jar xortrax/plugins/EtherDecompositor-0.1.0.jar" | sftp -P 49000 user@s1.dimc.cloud

clean:
	gradle clean
