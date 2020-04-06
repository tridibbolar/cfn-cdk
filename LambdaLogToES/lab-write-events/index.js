var AWS = require('aws-sdk');
AWS.config.update({region: 'ap-southeast-1'});

var dynamodb = new AWS.DynamoDB({apiVersion: '2012-08-10'});
 
exports.handler = function(event, context) {
    console.log(JSON.stringify(event, null, '  '));
    const tableName = process.env.TABLE_NAME;
	event.Records.forEach(record => {
		var payload = JSON.parse(record.body);
		console.log(record.body);

		console.log("id ", payload.id);
		console.log("name ", payload.name);
		
		var params = {
		  TableName: tableName,
		  Item: {
			'pk' : {'S': payload.id},
			'sk' : {'S': payload.name},
			'pnr': {'S': payload.pnr}
		  }
		};

		dynamodb.putItem(params, function(err, data) {
			if (err) {
				console.log("Error", err);
			} else {
				console.log("Success", data);
			}
		});
	});
};
