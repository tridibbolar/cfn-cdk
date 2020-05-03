package com.tcg.stk;

import java.util.stream.Collectors;

import software.amazon.awscdk.core.CfnOutput;
import software.amazon.awscdk.core.Construct;
import software.amazon.awscdk.core.Stack;
import software.amazon.awscdk.core.StackProps;
import software.amazon.awscdk.services.ec2.Vpc;
import software.amazon.jsii.JsiiObjectRef;

public class VpcStack extends Stack {

	public VpcStack(Construct scope, String id) {
		this(scope, id, null);
	}

	public VpcStack(Construct scope, String id, StackProps props) {
		super(scope, id, props);

		Vpc vpc = Vpc.Builder.create(this, "MyVpc").maxAzs(3) // Default is all AZs in region
				.build();

		CfnOutput.Builder.create(this, "VpcId").value(vpc.getVpcId()).exportName("VpcId").build();
		
		CfnOutput.Builder.create(this, "AvailabilityZones").value(vpc.getAvailabilityZones()
				.stream()
				.collect(Collectors.joining(",")))
		.exportName("AvailabilityZones").build();
		
		CfnOutput.Builder.create(this, "VpcCidrBlock").value(vpc.getVpcCidrBlock()).exportName("VpcCidrBlock").build();
		
		CfnOutput.Builder.create(this, "VpcDefaultNetworkAcl").value(vpc.getVpcDefaultNetworkAcl()).exportName("VpcDefaultNetworkAcl").build();
		
		CfnOutput.Builder.create(this, "VpcDefaultSecurityGroup").value(vpc.getVpcDefaultSecurityGroup()).exportName("VpcDefaultSecurityGroup").build();
		
		CfnOutput.Builder.create(this, "PrivateSubnets").value(vpc.getPrivateSubnets().toString()).exportName("PrivateSubnets").build();
		
		CfnOutput.Builder.create(this, "PublicSubnets").value(vpc.getPublicSubnets().toString()).exportName("PublicSubnets").build();
	}

}
