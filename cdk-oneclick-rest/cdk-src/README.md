# Welcome to your CDK Java project!

This project provision minimum infrastructure to deploy template REST service into ECS Fargate using codepipeline using `CDK` and `Java`.

It is a [Maven](https://maven.apache.org/) based project, so you can open this project with any Maven compatible Java IDE to build and run tests.

## Stacks
 * *CodecommitStack*		Create codecommit repository
 * *EcrStack*				Create ecr image repository
 * *CodepipelineStack*		Create code pipeline with source and build stages. Source stage read code from codecommit repository and build stage compile source code, build docker image and push it into ecr repository
 * *EcsFargateStack*		Provision ecs fargate and deploy docker image from above ecr repository
 * *VpcStack*				Provision standard vpc if require

## Prerequisites
 * You need following executables in your machine
 - `maven`
 - `cdk`
 - `java`
 - `git`
 * You need internet connections
 * Your role need IAM permissions to create codecommit, ecr
 
## Useful commands

 * `mvn package`     compile and run tests
 * `cdk ls`          list all stacks in the app
 * `cdk synth`       emits the synthesized CloudFormation template
 * `cdk deploy`      deploy this stack to your default AWS account/region
 * `cdk diff`        compare deployed stack with current state
 * `cdk docs`        open CDK documentation
 
 * `cdk synth CodecommitStack -o cdk.out`		Generate cloudformation script to create codecommit repository	
 * `cdk synth ECRStack -o cdk.out`		Generate cloudformation script to create ecr image repository	
 * `cdk synth * -o cdk.out`		Generate cloudformation script for all above stacks
 * `cdk --app cdk.out deploy CodecommitStack --parameters "CodecommitStack:coderepositoryname=tcg-one-clk"`		Create **tcg-one-clk** codecommit repository
 * `cdk --app cdk.out deploy ECRStack --parameters "ECRStack:ecrrepositoryname=tcg-one-clk"`	Create **tcg-one-clk** ecr image repository
 * `cdk --app cdk.out deploy CodepipelineStack --parameters "CodepipelineStack:coderepositoryname=tcg-one-clk"`		Create codepipeline
 * execute `git` commands to push your project into codecommit repository
  git init
  git add -A
  git commit -m "Initial commit of ${artifactId} project"
  git remote add origin https://git-codecommit.${awsregion}.amazonaws.com/v1/repos/${service}Repo
  git push origin master
 * `cdk --app cdk.out deploy EcsFargatteStack --parameters "EcsFargatteStack:ecrrepositoryname=tcg-one-clk" --parameters "EcsFargatteStack:vpcid=vpc-07a9ba3b47f2c734c" --parameters "EcsFargatteStack:imagetag=latest" --parameters "EcsFargatteStack:containerport=9090"`		Deploy docker image into ECS Fargate
 
 
 

Enjoy!
