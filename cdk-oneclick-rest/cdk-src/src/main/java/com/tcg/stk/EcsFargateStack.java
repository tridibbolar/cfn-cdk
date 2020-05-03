package com.tcg.stk;

import java.util.ArrayList;

import software.amazon.awscdk.core.CfnParameter;
import software.amazon.awscdk.core.Construct;
import software.amazon.awscdk.core.Fn;
import software.amazon.awscdk.core.Stack;
import software.amazon.awscdk.core.StackProps;
import software.amazon.awscdk.services.ec2.CfnInstance.CpuOptionsProperty;
import software.amazon.awscdk.services.ecr.RepositoryAttributes;
import software.amazon.awscdk.services.ec2.IVpc;
import software.amazon.awscdk.services.ec2.InstanceClass;
import software.amazon.awscdk.services.ec2.InstanceSize;
import software.amazon.awscdk.services.ec2.InstanceType;
import software.amazon.awscdk.services.ec2.SubnetSelection;
import software.amazon.awscdk.services.ec2.Vpc;
import software.amazon.awscdk.services.ec2.VpcLookupOptions;
import software.amazon.awscdk.services.ecs.AddCapacityOptions;
import software.amazon.awscdk.services.ecs.Cluster;
import software.amazon.awscdk.services.ecs.Compatibility;
import software.amazon.awscdk.services.ecs.ContainerDefinition;
import software.amazon.awscdk.services.ecs.ContainerDefinitionOptions;
import software.amazon.awscdk.services.ecs.ContainerImage;
import software.amazon.awscdk.services.ecs.Ec2Service;
import software.amazon.awscdk.services.ecs.PlacementConstraint;
import software.amazon.awscdk.services.ecs.PlacementStrategy;
import software.amazon.awscdk.services.ecs.PortMapping;
import software.amazon.awscdk.services.ecs.Protocol;
import software.amazon.awscdk.services.ecs.TaskDefinition;
import software.amazon.awscdk.services.ecs.patterns.ApplicationLoadBalancedFargateService;
import software.amazon.awscdk.services.ecs.patterns.ApplicationLoadBalancedTaskImageOptions;
import software.amazon.awscdk.services.elasticloadbalancingv2.ApplicationProtocol;

public class EcsFargateStack extends Stack {

	public EcsFargateStack(Construct scope, String id) {
		this(scope, id, null);
	}

	public EcsFargateStack(Construct scope, String id, StackProps props) {
		super(scope, id, props);

		CfnParameter ecrRepositoryName = CfnParameter.Builder.create(this, "ecrrepositoryname")
		        .description("Name of the ecr repository that can be passed in at deploy-time.")
		        .type("String").build();
		
		/*CfnParameter vpcId = CfnParameter.Builder.create(this, "vpcid")
		        .description("Existing vpc id")
		        .type("String").build();*/
		
		CfnParameter imageTag = CfnParameter.Builder.create(this, "imagetag")
		        .description("ecr image tag")
		        .type("String").build();
		
		CfnParameter containerPort = CfnParameter.Builder.create(this, "containerport")
		        .description("Container port")
		        .type("Number").build();
		
		
		String vpcIdstr = "vpc-07a9ba3b47f2c734c"; // Fn.importValue("VpcStack.VpcId");
		/*if (vpcId != null && vpcId.getValueAsString() != null && !vpcId.getValueAsString().trim().equalsIgnoreCase("")) {
			vpcIdstr = vpcId.getValueAsString();
		}*/

		/*System.out.println(Fn.importValue("VpcStack.VpcId"));
		System.out.println(Fn.importValue("AvailabilityZones"));
		System.out.println(Fn.importValue("ecr-repository-name"));*/

		VpcLookupOptions vpcLookupOptions = VpcLookupOptions.builder().vpcId(vpcIdstr).build();
		IVpc vpc = Vpc.fromLookup(this, "VpcForFargate", vpcLookupOptions);

		Cluster cluster = Cluster.Builder.create(this, "OneClickCluster").vpc(vpc).build();

		//RepositoryAttributes r = RepositoryAttributes.builder().
		software.amazon.awscdk.services.ecr.IRepository ecrRepo = software.amazon.awscdk.services.ecr.Repository
				.fromRepositoryName(this, "ecr-repo", ecrRepositoryName.getValueAsString());
		ContainerImage image = ContainerImage.fromEcrRepository(ecrRepo,imageTag.getValueAsString());

		// Create a load-balanced Fargate service and make it public
		ApplicationLoadBalancedFargateService.Builder.create(this, "OneClickFargateService").cluster(cluster) // Required
				.cpu(512) // Default is 256
				.desiredCount(6) // Default is 1
				.taskImageOptions(ApplicationLoadBalancedTaskImageOptions.builder().image(image)
						.containerName("c1")
						.containerPort(containerPort.getValueAsNumber())
						.build())
				.listenerPort(80)
				.protocol(ApplicationProtocol.HTTP)
				.memoryLimitMiB(2048) // Default is 512
				.publicLoadBalancer(true) // Default is false
				.build();
	}

}
