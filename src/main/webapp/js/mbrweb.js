/**
 * 
 */
var app = angular.module('mbrWebApp', ['ngMaterial']);

app.controller('IdController', ['$http', function($http) {
	this.lookupId = "";
	this.visitors = [];

	this.getVisitorInfo = function() {
		if (this.lookupId != "") {
			$http.get(contextPath + "/lookup/" + this.lookupId)
			.then(angular.bind(this, function(response) {
				this.visitors.push(response.data);
			}), angular.bind(this, function(response) {
				this.alertMessage = response.status + "; " + response.statusText;
			}));
		}

		this.lookupId = "";
	};
}]);
