package com.tcg.stk;

import software.amazon.awscdk.core.CfnOutput;
import software.amazon.awscdk.core.CfnParameter;
import software.amazon.awscdk.core.Construct;
import software.amazon.awscdk.core.Fn;
import software.amazon.awscdk.core.RemovalPolicy;
import software.amazon.awscdk.core.Stack;
import software.amazon.awscdk.core.StackProps;
import software.amazon.awscdk.services.ecr.Repository;

public class EcrStack extends Stack {
    public EcrStack(final Construct scope, final String id) {
        this(scope, id, null);
    }

    public EcrStack(final Construct scope, final String id, final StackProps props) {
        super(scope, id, props);

        CfnParameter ecrRepositoryName = CfnParameter.Builder.create(this, "ecrrepositoryname")
        .description("Name of the ecr repository that can be passed in at deploy-time.")
        .type("String").build();

        Repository repo = Repository.Builder.create(this, "ecr-repository")
                .repositoryName(ecrRepositoryName.getValueAsString())
                .removalPolicy(RemovalPolicy.DESTROY).build();

        CfnOutput.Builder
                .create(this, "repository-name")
                .value(repo.getRepositoryName())
                .exportName("ecr-repository-name")
                .build();

        CfnOutput.Builder
                .create(this, "repository-arn")
                .value(repo.getRepositoryArn())
                .exportName("ecr-repository-arn")
                .build();

        CfnOutput.Builder
                .create(this, "repository-uri")
                .value(repo.getRepositoryUri())
                .exportName("ecr-repository-uri")
                .build();

    }

}