<html>
	<head>
		<link rel="stylesheet" href="http://ajax.googleapis.com/ajax/libs/angular_material/1.1.4/angular-material.min.css">
		<script type="text/javascript">
			var contextPath = "${pageContext.request.contextPath}";
		</script>
		<script src="js/angular/angular.min.js"></script>
		<script src="js/angular/angular-animate.min.js"></script>
		<script src="js/angular/angular-aria.min.js"></script>
		<script src="js/angular/angular-messages.min.js"></script>
		<script src="https://ajax.googleapis.com/ajax/libs/angular_material/1.1.4/angular-material.min.js"></script>
		<script src="js/mbrweb.js"></script>
	</head>
	<body ng-app="mbrWebApp" ng-controller="IdController as idController">
		<div layout="row" class="md-inline-form">
				<md-content layout-padding>
					<form ng-submit="idController.getVisitorInfo()">
						<md-input-container class="md-block" flex-xs>
							<label>ID</label>
							<input type="text" ng-model="idController.lookupId" autofocus ng-trim="true">
						</md-input-container>
					</form>
				</md-content>
		</div>

		<div id="alertDiv">{{idController.alertMessage}}</div>

		<table>
			<thead>
				<tr>
					<th>Time</th>
					<th>#</th>
					<th>Name</th>
					<th>Notes</th>
				</tr>
			</thead>
			<tbody>
				<tr ng-repeat="visitor in idController.visitors | orderBy:'-timestamp'" ng-style="{'background-color': visitor.ok ? (visitor.notes == '' ? '#00ff00' : '#ffff00') : '#ff0000'}">
					<td>{{visitor.timestamp | date : "yyyy/M/d hh:mm:ssa"}}</td>
					<td>{{("0000" + visitor.memberId).slice(-4)}}</td>
					<td>{{visitor.lastName}}, {{visitor.firstName}}</td>
					<td>{{visitor.notes}}</td>
				</tr>
			</tbody>
		</table>
	</body>
</html>
