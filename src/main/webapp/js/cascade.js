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

// bind tag takes care of the dependency as an adjunct

function makeStaplerProxy(url,crumb,methods) {
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
