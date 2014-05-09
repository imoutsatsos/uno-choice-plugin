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
	        	var newValues = data[1];
	        	
	            // http://stackoverflow.com/questions/6364748/change-the-options-array-of-a-select-list
	            var oldSel = cascade.paramElement;
	            // clear()
	            if (oldSel.tagName == 'SELECT') { // handle SELECT's
		            while (oldSel.options.length > 0) {
		                oldSel.remove(oldSel.options.length - 1);
		            }
	        
		            for (i = 0; i < newValues.length; i++) {
		                var opt = document.createElement('option');
		                var entry = newValues[i];
		                if (!entry instanceof String) {
		                    opt.text = JSON.stringify(entry);
		                    opt.value = JSON.stringify(entry);
		                } else {
		                    opt.text = entry;
		                    opt.value = entry;
		                }
		                oldSel.add(opt, null);
		            }
		            var originalArray = [];
	                for (i = 0; i < cascade.paramElement.options.length; ++i) {
	                    originalArray.push(cascade.paramElement.options[i].innerHTML);
	                }
                	cascade.paramElement.originalOptions = originalArray;
                	
                	// Update original values, used in the index.jelly
                	var originalArray = [];
	                for (i = 0; i < cascade.paramElement.options.length; ++i) {
	                    originalArray.push(cascade.paramElement.options[i].innerHTML);
	                }
                	cascade.paramElement.originalOptions = originalArray;
                	if (oldSel.getAttribute('multiple') == 'multiple')
                	   oldSel.setAttribute('size', (newValues.length > 10 ? 10 : newValues.length) + 'px');
                } else if (oldSel.tagName == 'DIV') {
                	if (oldSel.children.length > 0 && oldSel.children[0].tagName == 'TABLE') {
                		var table = oldSel.children[0];
                		var tbody = table.children[0];
                		
                		trs = findElementsBySelector(tbody, 'tr', false);
                		if (tbody) {
                    		for (i = 0; i < trs.length; i++) {
                    			tbody.removeChild(trs[i]);
                    		}
                    	} else {
                    	   tbody = document.createElement('tbody');
                    	   table.appendChild(tbody);
                    	}
                		
                		var originalArray = [];
                		// Check whether it is a radio or checkbox element
                		if (oldSel.className == 'dynamic_checkbox') {
    	                	for (i = 0; i < newValues.length; i++) {
    	                		var entry = newValues[i];
    	                		// <TR>
    			                var tr = document.createElement('tr');
    			                var idValue = 'ecp_' + cascade.paramName + '_' + i;
    			                idValue = idValue.replace(' ', '_');
    			                tr.setAttribute('id', idValue);
    			                tr.setAttribute('style', 'white-space:nowrap');
    			                // <TD>
    			                var td = document.createElement('td');
    			                // <INPUT>
    			                var input = document.createElement('input');
    			                // <LABEL>
    			                var label = document.createElement('label');
    			                
    			                if (!entry instanceof String) {
    			                	input.setAttribute('json', JSON.stringify(entry));
    			                	input.setAttribute('name', 'value');
    			                	input.setAttribute("value", JSON.stringify(entry));
    			                	input.setAttribute("class", " ");
    			                	input.setAttribute("type", "checkbox");
    			                	label.className = "attach-previous";
    			                	label.innerHTML = JSON.stringify(entry);
    			                } else {
    			                    input.setAttribute('json', entry);
    			                	input.setAttribute('name', 'value');
    			                	input.setAttribute("value", entry);
    			                	input.setAttribute("class", " ");
    			                	input.setAttribute("type", "checkbox");
    			                	label.className = "attach-previous";
    			                	label.innerHTML = entry;
    			                }
    			                
    			                originalArray.push(entry);
    			                
    			                // Put everything together
    			                td.appendChild(input);
    			                td.appendChild(label);
    			                tr.appendChild(td);
    			                tbody.appendChild(tr);
    			            }
    			            
    			            cascade.paramElement.originalOptions = originalArray;
    			        } else {
    			             for (i = 0; i < newValues.length; i++) {
                                var entry = newValues[i];
                                // <TR>
                                var tr = document.createElement('tr');
                                var idValue = 'ecp_' + cascade.paramName + '_' + i;
                                idValue = idValue.replace(' ', '_');
                                tr.setAttribute('id', idValue);
                                tr.setAttribute('style', 'white-space:nowrap');
                                // <TD>
                                var td = document.createElement('td');
                                // <INPUT>
                                var input = document.createElement('input');
                                // <LABEL>
                                var label = document.createElement('label');
                                
                                if (!entry instanceof String) {
                                    input.setAttribute('json', JSON.stringify(entry));
                                    input.setAttribute('name', 'value');
                                    input.setAttribute("value", JSON.stringify(entry));
                                    input.setAttribute("class", " ");
                                    input.setAttribute("type", "radio");
                                    label.className = "attach-previous";
                                    label.innerHTML = JSON.stringify(entry);
                                } else {
                                    input.setAttribute('json', entry);
                                    input.setAttribute('name', 'value');
                                    input.setAttribute("value", entry);
                                    input.setAttribute("class", " ");
                                    input.setAttribute("type", "radio");
                                    label.className = "attach-previous";
                                    label.innerHTML = entry;
                                }
                                
                                originalArray.push(entry);
                                
                                // Put everything together
                                td.appendChild(input);
                                td.appendChild(label);
                                tr.appendChild(td);
                                tbody.appendChild(tr);
                            }
                            cascade.paramElement.originalOptions = originalArray;
    			        } // if (oldSel.className == 'dynamic_checkbox') 
    			        oldSel.style.height = '' + (23 * (newValues.length > 10 ? 10 : newValues.length)) + 'px';
	                } // if (oldSel.children.length > 0 && oldSel.children[0].tagName == 'TABLE') 
                } // if (oldSel.tagName == 'SELECT') { // else if (oldSel.tagName == 'DIV') {
	        });// proxy call
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

// bind tag takes care of the dependency as an adjunct

function makeStaplerProxy2(url,crumb,methods) {
    if (url.substring(url.length - 1) !== '/') url+='/';
    var proxy = {};

    var stringify;
    if (Object.toJSON) // needs to use Prototype.js if it's present. See commit comment for discussion
        stringify = Object.toJSON;  // from prototype
    else if (typeof(JSON)=="object" && JSON.stringify)
        stringify = JSON.stringify; // standard

    var genMethod = function(methodName) {
        proxy[methodName] = function() {
            var args = arguments;

            // the final argument can be a callback that receives the return value
            var callback = (function(){
                if (args.length==0) return null;
                var tail = args[args.length-1];
                return (typeof(tail)=='function') ? tail : null;
            })();

            // 'arguments' is not an array so we convert it into an array
            var a = [];
            for (var i=0; i<args.length-(callback!=null?1:0); i++)
                a.push(args[i]);

            if(window.jQuery === window.$) { //Is jQuery the active framework?
                $.ajax({
                    type: "POST",
                    url: url+methodName,
                    data: stringify(a),
                    contentType: 'application/x-stapler-method-invocation;charset=UTF-8',
                    headers: {'Crumb':crumb},
                    dataType: "json",
                    async: "false",
                    success: function(data, textStatus, jqXHR) {
                        if (callback!=null) {
                            var t = {};
                            t.responseObject = function() {
                                return data;
                            };
                            callback(t);
                        }
                    }
                });
            } else { //Assume prototype should work
                new Ajax.Request(url+methodName, {
                    method: 'post',
                    requestHeaders: {'Content-type':'application/x-stapler-method-invocation;charset=UTF-8','Crumb':crumb},
                    postBody: stringify(a),
                    asynchronous: false,
                    onSuccess: function(t) {
                        if (callback!=null) {
                            t.responseObject = function() {
                                return eval('('+this.responseText+')');
                            };
                            callback(t);
                        }
                    }
                });
            }
        }
    };

    for(var mi = 0; mi < methods.length; mi++) {
        genMethod(methods[mi]);
    }

    return proxy;
}
