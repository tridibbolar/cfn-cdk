#@ECHO OFF
ECHO Cleaning project folder...
ECHO .
call mvn clean compile
ECHO .
CALL cdk ls
ECHO .
CALL cdk --app cdk.out deploy CodecommitStack --parameters "CodecommitStack:coderepositoryname=tcg-one-clk"
ECHO .
CALL cdk --app cdk.out deploy ECRStack --parameters "ECRStack:ecrrepositoryname=tcg-one-clk"
ECHO .
CALL cdk --app cdk.out deploy CodepipelineStack --parameters "CodepipelineStack:coderepositoryname=tcg-one-clk"
ECHO
CALL cd ..
ECHO .
CALL mvn archetype:generate -DarchetypeGroupId=com.tcg.cloud -DarchetypeArtifactId=mcrsvc-archetype -DarchetypeVersion=0.0.1-SNAPSHOT -DgroupId=com.tcg.micro.rest -DartifactId=rest-template -Dresource=Employee -Drestpath=employee -Dawsaccountid=086272791573 -Dawsregion=ap-southeast-1 -Ddockerimagename=tcg-one-clk -Dimgversion=v1
ECHO .
CALL cd rest-template
CALL git init
CALL git add -A
CALL git commit -m "Initial commit of rest-template project"
CALL git remote add origin https://admin_ual+1-at-086272791573:Jov4FynKpT0DuOBecLmy+wjeKVU8nrOwj5dYkM0qw3w=@git-codecommit.ap-southeast-1.amazonaws.com/v1/repos/tcg-one-clk
CALL git push origin master
ECHO .
CALL cd ..\cdk-src
ECHO .
CALL cdk --app cdk.out deploy EcsFargateStack --parameters "EcsFargateStack:ecrrepositoryname=tcg-one-clk" --parameters "EcsFargateStack:imagetag=v1" --parameters "EcsFargateStack:containerport=9090"