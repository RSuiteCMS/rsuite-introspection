RSuite.Action({
	id: '@pluginId@:actionMenuManifest',
	icon: 'action_global',
	invoke: function (context) {
		RSuite.services({ service: 'api/@pluginId@:actionMenus', json: true })
			.done(function (items) {
				var dlg = $('<div>').css({ position: 'absolute', pointerEvents: 'none', visibility: 'hidden', top: 0, left: 0, fontSize: '0.9em' });
				var tbl = $('<table>').appendTo(dlg);

				$('<thead>').append(
					$('<tr>').append(
						$('<th align="left">').text('Scopes'),
						$('<th align="left">').text('Menu item'),
						$('<th align="left">').text('Action'),
						$('<th align="left">').text('ID'),
						$('<th align="left">').text('group'),
						$('<th align="left">').text('formId'),
						$('<th align="left">').text('service')
					)
				).appendTo(tbl);
				var tblBody = $('<tbody>').appendTo(tbl);
				dlg.appendTo(document.body);

				tblBody.append(items.map(function (item, index) {
					var action = RSuite.Action.get(item.actionName) || Ember.Object.create();
					var label = RSuite.messageTable.getIfMessage(item.label) || action.get('label');
					var iconDOM = $(RSuite.Icon.getIcon(item.propertyMap['rsuite-icon'] || item.propertyMap['rsuite:icon'] || action.get('icon') || '$blank').asDOM(24));
					iconDOM.css({
						display: 'inline-block',
						verticalAlign: 'middle',
						marginRight: '0.25em'
					});
					var path = RSuite.messageTable.getIfMessage(item.propertyMap['rsuite-path'] || item.propertyMap['rsuite:path'] || action.get('path'));
					var entry = $('<tr>');
					entry.css({ backgroundColor: (index % 2) ? '#ebf3fd' : '#FFFFFF' });
					entry.append(
						$('<td>').html(item.scope.split(',').join('<br />\n')),
						$('<td>').append(iconDOM, $('<label>').html(label)),
						$('<td>').text(item.actionName),
						$('<td>').text(item.id),
						$('<td>').text(item.group),
						$('<td>').text(item.propertyMap.formId),
						$('<td>').text(item.propertyMap.remoteApiName)
					);
					return entry;
				}));
				var width = Math.min(dlg.width() + 64, document.body.clientWidth);
				dlg.remove();
				dlg.css({ position: '', pointerEvents: '', visibility: '', top: '', left: '' });
				dlg.width(width - 64);
				dlg.dialog({ title: "Actions manifest", width: width });
			});
	}
});

RSuite.Action({
	id: "@pluginId@:iconManifest",
	icon: 'preview',
	invoke: function (context) {
		RSuite.Icon.getIconManifest();
	}
});

RSuite.Action({
	id: "@pluginId@:colors",
	icon: 'dialog_information',
	invoke: function () {
		RSuite.less.getPaletteManifest();
	}
});
RSuite.Action({
	id: "@pluginId@:actionsManifest",
	icon: "action",
	invoke: function () {
		RSuite.Action.getActionManifest();
	}
});
