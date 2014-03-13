function addTimeUnitsTableRow() {
  var table = getIxTableById('timeUnitsTable');
  var rowIndex = table.addRow([ '', false, 0, '', '' ]);
  
  if (rowIndex == 0) {
    table.setCellValue(0, table.getNamedColumnIndex("baseUnit"), true);
    table.setCellValue(0, table.getNamedColumnIndex("baseUnits"), 1);
    table.hideCell(0, table.getNamedColumnIndex("baseUnits"));
  }

  $('noTimeUnitsAddedMessageContainer').setStyle({
    display : 'none'
  });
}

function onLoad(event) {
  var timeUnitsTable = new IxTable($('timeUnitsTable'), {
    id : "timeUnitsTable",
    columns : [
      {
        left : 8,
        width : 30,
        dataType : 'button',
        imgsrc : GLOBAL_contextPath + '/gfx/accessories-text-editor.png',
        tooltip : getLocale().getText("settings.timeUnits.timeUnitsTableEditTooltip"),
        onclick : function(event) {
          var table = event.tableComponent;
          for ( var i = 0; i < table.getColumnCount(); i++) {
            table.setCellEditable(event.row, i, table.isCellEditable(event.row, i) == false);
          }
        }
      }, {
        header : getLocale().getText("settings.timeUnits.timeUnitsTableBaseUnitHeader"),
        left : 38,
        width : 80,
        dataType : 'checkbox',
        editable : true,
        paramName : 'baseUnit'
      }, {
        header : getLocale().getText("settings.timeUnits.timeUnitsTableBaseUnitsHeader"),
        left : 126,
        width : 100,
        dataType : 'number',
        editable : true,
        paramName : 'baseUnits',
        required : true
      }, {
        header : getLocale().getText("settings.timeUnits.timeUnitsTableNameHeader"),
        left : 234,
        right : 46,
        dataType : 'text',
        editable : true,
        paramName : 'name',
        required : true
      }, {
        right : 8,
        width : 30,
        dataType : 'button',
        imgsrc : GLOBAL_contextPath + '/gfx/list-remove.png',
        tooltip : getLocale().getText("settings.timeUnits.timeUnitsTableRemoveTooltip"),
        onclick : function(event) {
          event.tableComponent.deleteRow(event.row);
          if (event.tableComponent.getRowCount() == 0) {
            $('noTimeUnitsAddedMessageContainer').setStyle({
              display : ''
            });
          }
        },
        paramName : 'removeButton',
      } ]
  });

  timeUnitsTable.addListener("cellValueChange", function(event) {
    if ((event.value == true) && !this._settingValue) {
      var baseUnitColumn = event.tableComponent.getNamedColumnIndex("baseUnit");
      var baseUnitsColumn = event.tableComponent.getNamedColumnIndex("baseUnits");

      if (baseUnitColumn == event.column) {
        this._settingValue = true;
        try {
          for ( var i = 0, l = event.tableComponent.getRowCount(); i < l; i++) {
            if (i != event.row) {
              event.tableComponent.setCellValue(i, baseUnitColumn, false);
              event.tableComponent.showCell(i, baseUnitsColumn);
            } else {
              event.tableComponent.setCellValue(i, baseUnitColumn, true);
              event.tableComponent.hideCell(i, baseUnitsColumn);
            }
          }
        } finally {
          this._settingValue = false;
        }
      }
    }
  });

  if (timeUnitsTable.getRowCount() > 0) {
    $('noTimeUnitsAddedMessageContainer').setStyle({
      display : 'none'
    });
  } else {
    addTimeUnitsTableRow();
  }
}