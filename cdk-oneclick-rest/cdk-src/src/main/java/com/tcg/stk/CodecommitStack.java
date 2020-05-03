package com.tcg.stk;

import software.amazon.awscdk.core.CfnOutput;
import software.amazon.awscdk.core.CfnParameter;
import software.amazon.awscdk.core.Construct;
import software.amazon.awscdk.core.Stack;
import software.amazon.awscdk.core.StackProps;
import software.amazon.awscdk.services.codecommit.Repository;

public class CodecommitStack extends Stack {
    public CodecommitStack(final Construct scope, final String id) {
        this(scope, id, null);
    }

    public CodecommitStack(final Construct scope, final String id, final StackProps props) {
        super(scope, id, props);
        
        CfnParameter codeRepositoryName = CfnParameter.Builder.create(this, "coderepositoryname")
                                    .description("Name of the code repository that can be passed in at deploy-time.")
                                    .type("String")    
                                    .build();

        Repository repo = Repository.Builder
                .create(this, "code-repository")
                .repositoryName(codeRepositoryName.getValueAsString())      //"svc-code-repo"
                .description("This repository contains SpringBoot service code")
                .build();

        CfnOutput.Builder
                .create(this, "HttpUrl")
                .value(repo.getRepositoryCloneUrlHttp())
                .exportName("HttpUrl")
                .build();

        CfnOutput.Builder
                .create(this, "SshUrl")
                .value(repo.getRepositoryCloneUrlSsh())
                .exportName("SshUrl")
                .build();
    }
}