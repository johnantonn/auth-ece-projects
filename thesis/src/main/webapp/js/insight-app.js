var app = angular.module('insight-app', ['angular-jqcloud']);	
	
	app.controller('MainCtrl', ['$http', function($http){
		
		var self = this;
		
		//Query parameters
		self.query;
		self.docTopicIds = new Array();
		self.parTopicIds = new Array();
		
		//ng-show parameters
		self.showQuery = true;
		self.showDocTopics = false;
		self.showParTopics = false;
		self.showParagraphs = false;
		
		//UI parameters
		self.paragraphs;
		self.docTopics = [];
		self.parTopics = [];
		self.colors = [];
		self.notFound = [
			{
			"topWordsList":
			[{text: "not Found", weight: 13},
			{text: "not Found", weight: 10.5},
			{text: "not Found", weight: 9.4},
			{text: "not Found", weight: 8},
			{text: "not Found", weight: 6.2},
			{text: "not Found", weight: 5},
			{text: "not Found", weight: 5},
			{text: "not Found", weight: 5},
			{text: "not Found", weight: 5},
			{text: "not Found", weight: 4},
			{text: "not Found", weight: 4},
			{text: "not Found", weight: 3},
			{text: "not Found", weight: 3},
			{text: "not Found", weight: 3},
			{text: "not Found", weight: 3},
			{text: "not Found", weight: 3},
			{text: "not Found", weight: 3},
			{text: "not Found", weight: 3},
			{text: "not Found", weight: 2},
			{text: "not Found", weight: 2}]
			}
		];
		
		//Parameters functions
		self.isSelected = function(id, idList) {
			var i;
			for (i = 0; i < idList.length; i++) {
				if (angular.equals(idList[i], id)) {
					return true;
				}
			}
			return false;
		};
		
		self.toggleId = function(id, idList){
			if(self.isSelected(id, idList)){
				var index = idList.indexOf(id);
				idList.splice(index,1);
				alert('You removed topic '+id);
				self.colors = [];
			}
			else{
				idList.push(id);
				alert('You selected topic '+id);
				self.colors = ["#800026", "#bd0026", "#e31a1c", "#fc4e2a", "#fd8d3c", "#feb24c", "#fed976"];
			}	
		};
		
		/**
		$http functions*/
		//submit query
		self.submitQuery = function(){
			$http.get('http://localhost:8080/insight/api/documentTopics', {params: {q:self.query}}).then(function(response){
				if(response.data == null){
					self.docTopics = self.notFound;
				}
				else{
					self.docTopics = response.data;
				}
				self.showQuery = false;
				self.showDocTopics = true;
			}, function(errResponse){
				alert('Error while fetching document topic clouds');
				console.error('Error while fetching document topic clouds');
				});
			};
			
		//submit document topics	
		self.submitDocTopics = function(){
			$http.get('http://localhost:8080/insight/api/paragraphTopics', {params: {topicIds:self.docTopicIds}}).then(function(response){
				if(response.data == null){
					self.parTopics = self.notFound;
				}
				else{
					self.parTopics = response.data;
				}
				self.showDocTopics = false;
				self.showParTopics = true;
			}, function(errResponse){
				alert('Error while fetching paragraph topic clouds');
				console.error('Error while fetching paragraph topic clouds');
				});
		};
		
		//submit paragraph topics
		self.submitParTopics = function(){
			$http.get('http://localhost:8080/insight/api/paragraphs', {params: {topicIds:self.parTopicIds}}).then(function(response){
				if(response.data == null){
					self.paragraphs = self.notFound;
				}
				else{
					self.paragraphs = response.data;	
				}
				self.showParTopics = false;
				self.showParagraphs = true;
			}, function(errResponse){
				alert('Error while fetching paragraphs');
				console.error('Error while fetching paragraphs');
				});
		};
		
	}]);