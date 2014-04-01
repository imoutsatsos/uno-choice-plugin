// global referencedParameters is an array with the jelly form value

function CascadeParameter(paramName, paramElement, proxy) {
	this.paramName = paramName;
	this.paramElement = paramElement;
	this.proxy = proxy;
	this.referencedParameters = [];
}

function ReferencedParameter(parameterName, parameterElement) {
	this.paramName = parameterName;
	this.paramElement = parameterElement;
	this.cascadeParameters = [];
	
	this.updateCascadeParameter = function (evt) {
		var cascadeParameters = this.cascadeParameters;
		for (var count = 0; count < cascadeParameters.length ; count ++) {
			var cascade = cascadeParameters[count];
			
			var params = new Array();
	        // get all parameter values
	        for (var i = 0; i < cascade.referencedParameters.length ; i++) {
	        	referencedParameter = cascade.referencedParameters[i];
	            value = getParameterValue(referencedParameter.paramName, referencedParameter.paramElement);
	            params.push(value);
	        }
	        paramsString = params.join(',');
	        
	        // call the doUpdate method
	        cascade.proxy.doUpdate(paramsString);
	        // update the select element
	        cascade.proxy.getChoices(count, function(t) {
	        	choices = t.responseText;
	        	var data = JSON.parse(choices);
	        	var cascade = cascadeParameters[data[0]];
	        	var newSel = data[1];
	        	
	            // http://stackoverflow.com/questions/6364748/change-the-options-array-of-a-select-list
	            var oldSel = cascade.paramElement;
	            // clear()
	            while (oldSel.options.length > 0) {
	                oldSel.remove(oldSel.options.length - 1);
	            }
	        
	            for (i = 0; i < newSel.length; i++)
	            {
	                var opt = document.createElement('option');
	                var entry = newSel[i];
	                if (!entry instanceof String) {
	                    opt.text = JSON.stringify(entry);
	                    opt.value = JSON.stringify(entry);
	                } else {
	                    opt.text = entry;
	                    opt.value = entry;
	                }
	                oldSel.add(opt, null);
	            }
	        });
	    } // for, count
	};
}

getParameterValue = function(name, e) {
    var value = '';
    if (e.nodeName != "INPUT" && e.getAttribute('type') != 'hidden') {
        if (e.nodeName == 'SELECT')
            value = getSelectValues(e);
        else
            value = e.value;
        if (value == '') // multi selects or radios not selected
            value = '';
    }
    return name + '=' + value;
}

// Return an array of the selected opion values
// select is an HTML select element
// From: http://stackoverflow.com/questions/5866169/getting-all-selected-values-of-a-multiple-select-box-when-clicking-on-a-button-u
getSelectValues = function(select) {
  var result = [];
  var options = select && select.options;
  var opt;

  for (var i=0, iLen=options.length; i<iLen; i++) {
    opt = options[i];

    if (opt.selected) {
      result.push(opt.value || opt.text);
    }
  }
  return result;
}
