var zend = zend || {};

zend.options = {
		enableNotifications : function(populate) {
			localStorage['options-enable-notifications'] = true;
			if (populate !== false) {
				zend.options.populate();
			}
		},
		
		disableNotifications : function(populate) {
			localStorage['options-enable-notifications'] = false;
			zend.options.populate();
		},
		
		isNotificationsEnabled : function() {
			var val = localStorage['options-enable-notifications'];
			return val === undefined ? true : val == "true";
		},
		
		populate : function() {
			var newVal = !zend.options.isNotificationsEnabled();
			var jn = $("#disableNotifications");
			var currVal = jn.attr('checked'); 
			if (currVal != newVal) {
				jn.attr('checked', newVal);
			}
		},
		
		fireChange : function(node) {
			console.log('fireChange');
			var jnode = $(node);
			var id = jnode.attr("id");
			var value = !jnode.attr('checked');
			if (id == 'disableNotifications') {
				if (value) {
					zend.options.enableNotifications(false);
				} else {
					zend.options.disableNotifications(false);
				}
			}
		}

};