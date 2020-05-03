package com.tcg.stk;

import software.amazon.awscdk.core.Construct;
import software.amazon.awscdk.core.Fn;
import software.amazon.awscdk.core.Stack;
import software.amazon.awscdk.core.StackProps;
import software.amazon.awscdk.services.ec2.IVpc;
import software.amazon.awscdk.services.ec2.Vpc;
import software.amazon.awscdk.services.ec2.VpcLookupOptions;
import software.amazon.jsii.JsiiObjectRef;

public class EcsEc2Stack extends Stack {

	public EcsEc2Stack(Construct scope, String id) {
		this(scope, id, null);
	}

	public EcsEc2Stack(Construct scope, String id, StackProps props) {
		super(scope, id, props);

		VpcLookupOptions vpcLookupOptions = VpcLookupOptions.builder().vpcId(Fn.importValue("VpcId")).build();
		IVpc vpc = Vpc.fromLookup(this, "VpcForFargate", vpcLookupOptions);
		
		/*AddCapacityOptions ecsClusterCapacity = AddCapacityOptions.builder()
		.allowAllOutbound(true)
		.desiredCapacity(2)
		.instanceType(InstanceType.of(InstanceClass.PARALLEL3, InstanceSize.SMALL))
		.vpcSubnets(SubnetSelection.builder()
				.subnets(vpc.getPublicSubnets())
				.build())
		.build();*/
		
		/*Cluster cluster = Cluster.Builder.create(this, "EcsCluster")
		.clusterName("aws-tcg-training")
		.capacity(ecsClusterCapacity)
		.build();*/
		
		//ArrayList<PlacementConstraint> placementConstraints = new ArrayList<PlacementConstraint>();
				//placementConstraints.add(PlacementConstraint.distinctInstances());
				
				/*TaskDefinition taskDefinition = TaskDefinition.Builder
						.create(this, "task-definition")
						//.placementConstraints(placementConstraints)
						.compatibility(Compatibility.FARGATE)
						.cpu("2")
						.memoryMiB("1024")
						.build();
				
				
				
				software.amazon.awscdk.services.ecr.IRepository ecrRepo = software.amazon.awscdk.services.ecr.Repository
						.fromRepositoryName(this, "ecr-repo", Fn.importValue("ecr-repository-name"));
				ContainerImage image = ContainerImage.fromEcrRepository(ecrRepo);
				ContainerDefinitionOptions containerDefinitionOpt = ContainerDefinitionOptions.builder()
						.privileged(true)
						.memoryReservationMiB(1024)
						.cpu(2)
						.image(image)
						.build();
				
				//ContainerDefinition cd = ContainerDefinition.Builder.create(this, "dd").`
				
				ContainerDefinition containerDefinition = taskDefinition.addContainer("container", containerDefinitionOpt);
				PortMapping portMappings = PortMapping.builder().containerPort(80).protocol(Protocol.TCP).build();
				containerDefinition.addPortMappings(portMappings);
				
				Ec2Service service = Ec2Service.Builder.create(this, "service")
						.cluster(cluster)
						.taskDefinition(taskDefinition)
						.serviceName("tcg-aws-training")
						.build();
				
				service.addPlacementStrategies(PlacementStrategy.packedByMemory(),PlacementStrategy.spreadAcrossInstances());*/
	}

}
