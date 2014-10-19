/**
 * The MIT License (MIT)
 * 
 * Copyright (c) <2014> <Ioannis Moutsatsos, Bruno P. Kinoshita>
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

'use strict';

/**
 * <h2>Uno Choice Javascript module.</h2>
 * 
 * <p>This Javascript module is used in Uno-Choice Plug-in, and was created to enable users to have different 
 * types of parameters in Jenkins.</p>
 * 
 * <p>In Jenkins parameters are used to customize Job variables. However, the range of parameters and their 
 * features is limited. Specially in the UI, as for example, elements that are updated reacting to changes in 
 * other elements (e.g. city and state combo boxes).</p>
 * 
 * <p>This module <strong>depends on JQuery</strong>, and on methods provided by Jenkins:</p>
 * 
 * <ul>
 *     <li>findElementsBySelector(startNode,selector,includeSelf) - looks for elements using YUI and DOM</li>
 * </ul>
 * 
 * @param $ jQuery global var
 * @author Bruno P. Kinoshita <brunodepaulak@yahoo.com.br>
 * @since 0.20
 */
var UnoChoice = UnoChoice || (function($) {
    // The final public object
    var instance = {};
    
    var SEPARATOR = '__LESEP__';
    
    // Plug-in classes

    // --- Cascade Parameter

    /**
     * A parameter that references parameters.
     * 
     * @param paramName parameter name
     * @param paramElement parameter HTML element
     * @param proxy Stapler proxy object that references the CascadeChoiceParameter
     */
    /* public */ function CascadeParameter(paramName, paramElement, proxy) {
        this.paramName = paramName;
        this.paramElement = paramElement;
        this.proxy = proxy;
        this.referencedParameters = [];
    }
    
    /**
     * Gets the parameter name.
     * 
     * @return <code>String</code> parameter name
     */
    CascadeParameter.prototype.getParameterName = function() {
        return this.paramName;
    }
    
    /**
     * Gets the parameter HTML element.
     * 
     * @return HTML element
     */
    CascadeParameter.prototype.getParameterElement = function() {
        return this.paramElement;
    }
    
    /**
     * Gets the array of referenced parameters.
     * 
     * @return Array of the ReferencedParameter's
     */
    CascadeParameter.prototype.getReferencedParameters = function() {
    	return this.referencedParameters;
    }
    
    /**
     * Used to create the request string that will update the cascade parameter values. Returns a
     * String, with name=value for each referenced parameter.
     * 
     * @return String with name=value for each referenced parameter
     */
    CascadeParameter.prototype.getReferencedParametersAsText = function() {
    	var parameterValues = new Array();
		// get the parameters' values
		for (var j = 0; j < this.getReferencedParameters().length; j++) {
			var referencedParameter = this.getReferencedParameters()[j];
			var name = referencedParameter.getParameterName();
			var value = getParameterValue(referencedParameter);
			parameterValues.push(name + '=' + value);
		}
		
		var parametersString = parameterValues.join(SEPARATOR);
		return parametersString;
    }
    
    /**
     * Updates the CascadeParameter object.
     * 
     * TODO: explain what happens here
     */
    CascadeParameter.prototype.update = function() {
    	var parametersString = this.getReferencedParametersAsText(); // gets the array parameters, joined by , (e.g. a,b,c,d)
    	console.log('Referenced parameters: ' + parametersString);
    	// Update the CascadeChoiceParameter Map of parameters
    	this.proxy.doUpdate(parametersString);
    	// Now we get the updated choices, after the Groovy script is eval'd using the updated Map of parameters
    	// The inner function is called with the response provided by Stapler. Then we update the HTML elements.
    	var _self = this; // re-reference this to use within the inner function
    	this.proxy.getChoicesForUI(function (t) {
    		var choices = t.responseText;
        	var data = JSON.parse(choices);
        	var newValues = data[0];
    	    var newKeys = data[1];
        	
        	var selectedElements = new Array();
        	// filter selected elements and create a matrix for selection
        	// some elements may have key or values with the suffix :selected
        	// we want to remove these suffixes
        	for (var i = 0; i < newValues.length; i++) {
        		var newValue = newValues[i];
        		if (newValue && newValue.endsWith(':selected')) {
        			selectedElements.push(i);
        			newValues[i] = newValues[i].substring(0, newValue.indexOf(':selected'));
        		}
        		
    			var newKey = newKeys[i];
    			if (newKey && typeof newKey == "string" && newKey.endsWith(':selected')) {
        			newKey[i] = newKey[i].substring(0, newKey.indexOf(':selected'));
        		}
        	}
        	
        	// FIXME
        	// http://stackoverflow.com/questions/6364748/change-the-options-array-of-a-select-list
            var parameterElement = _self.getParameterElement();
            if (parameterElement.tagName == 'SELECT') { // handle SELECT's
	            while (parameterElement.options.length > 0) {
	            	parameterElement.remove(parameterElement.options.length - 1);
	            }
        
	            for (i = 0; i < newValues.length; i++) {
	                var opt = document.createElement('option');
	                var value = newKeys[i];
	                var entry = newValues[i];
	                if (!entry instanceof String) {
	                    opt.text = JSON.stringify(entry);
	                    opt.value = JSON.stringify(value); //JSON.stringify(entry);
	                } else {
	                    opt.text = entry;
	                    opt.value = value;
	                }
	                if (selectedElements.indexOf(i) >= 0) {
	                	opt.setAttribute('selected', 'selected');
	                }
	                parameterElement.add(opt, null);
	            }
	            
	            //if (oldSel.getAttribute('multiple') == 'multiple') {
            	//   oldSel.setAttribute('size', (newValues.length > 10 ? 10 : newValues.length) + 'px');
            	//}
	            
	            // Update the values for the filtering
	            var originalArray = [];
                for (i = 0; i < _self.getParameterElement().options.length; ++i) {
                    originalArray.push(_self.getParameterElement().options[i].innerHTML);
                }
                if (_self.getParameterElement().filterElement) {
                	_self.getParameterElement().filterElement.setOriginalArray(originalArray);
                }
            } else if (parameterElement.tagName == 'DIV') {
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
			                
			                if (selectedElements.indexOf(i) >= 0) {
			                	input.setAttribute('checked', 'checked');
			                }
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
			            
			            // Update the values for the filtering
		                if (_self.paramElement.filterElement) {
		                	_self.paramElement.filterElement.setOriginalArray(originalArray);
		                }
			        } else { // radio
			             for (i = 0; i < newValues.length; i++) {
                            var entry = newValues[i];
                            // <TR>
                            var tr = document.createElement('tr');
                            var idValue = 'ecp_' + cascade.paramName + '_' + i;
                            idValue = idValue.replace(' ', '_');
                            //tr.setAttribute('id', idValue); // will use the ID for the hidden value element
                            tr.setAttribute('style', 'white-space:nowrap');
                            // <TD>
                            var td = document.createElement('td');
                            // <INPUT>
                            var input = document.createElement('input');
                            // <LABEL>
                            var label = document.createElement('label');
                            // <HIDDEN>
                            var hiddenValue = document.createElement('input');
                            
                            if (selectedElements.indexOf(i) >= 0) {
			                	input.setAttribute('checked', 'checked');
			                }
                            if (!entry instanceof String) {
                                input.setAttribute('json', JSON.stringify(entry));
                                input.setAttribute('name', cascade.paramName);
                                input.setAttribute("value", JSON.stringify(entry));
                                input.setAttribute("class", " ");
                                input.setAttribute("type", "radio");
                                input.setAttribute('onclick', 'radioButtonSelect("'+cascade.paramName+'", "'+idValue+'")');
                                label.className = "attach-previous";
                                label.innerHTML = JSON.stringify(entry);
                            } else {
                                input.setAttribute('json', entry);
                                input.setAttribute('name', cascade.paramName);
                                input.setAttribute("value", entry);
                                input.setAttribute("class", " ");
                                input.setAttribute("type", "radio");
                                input.setAttribute('onclick', 'radioButtonSelect("'+cascade.paramName+'", "'+idValue+'")');
                                label.className = "attach-previous";
                                label.innerHTML = entry;
                            }
                            
                            hiddenValue.setAttribute('json', entry);
                            hiddenValue.setAttribute('name', '');
                            hiddenValue.setAttribute("value", entry);
                            hiddenValue.setAttribute("class", cascade.paramName);
                            hiddenValue.setAttribute("type", "hidden");
                            hiddenValue.setAttribute('id', idValue);
                            
                            originalArray.push(entry);
                            
                            // Put everything together
                            td.appendChild(input);
                            td.appendChild(label);
                            td.appendChild(hiddenValue);
                            tr.appendChild(td);
                            tbody.appendChild(tr);
                            var endTr = document.createElement('tr');
                            endTr.setAttribute('style', 'display: none');
                            endTr.setAttribute('class', 'radio-block-end');
                            tbody.appendChild(endTr);
                        }
			            // Update the values for the filtering
		                if (_self.paramElement.filterElement) {
		                	_self.paramElement.filterElement.setOriginalArray(originalArray);
		                }
			        } // if (oldSel.className == 'dynamic_checkbox') 
			        oldSel.style.height = '' + (23 * (newValues.length > 10 ? 10 : newValues.length)) + 'px';
                } // if (oldSel.children.length > 0 && oldSel.children[0].tagName == 'TABLE') 
            } // if (oldSel.tagName == 'SELECT') { // else if (oldSel.tagName == 'DIV') {
    	});
    }

    // --- Referenced Parameter

    /**
     * <p>A parameter that is referenced by other parameters. Stores a list of cascade parameters, that reference this
     * parameter.</p>
     * 
     * <p>Whenever this parameter changes, it will notify each cascade parameter.</p>
     * 
     * @param paramName parameter name
     * @param paramElement parameter HTML element
     */
    /* public */ function ReferencedParameter(paramName, paramElement) {
    	this.paramName = paramName;
    	this.paramElement = paramElement;
    	this.cascadeParameters = [];
    }
    
    ReferencedParameter.prototype.getParameterName = function() {
    	return this.paramName;
    }
    
    ReferencedParameter.prototype.getParameterElement = function() {
    	return this.paramElement;
    }
    
    ReferencedParameter.prototype.updateCascadeParameters = function() {
    	for (var i = 0; i < this.cascadeParameters.length ; i++) {
    		this.cascadeParameters[i].update();
    	}
    }

    // --- Filter Element

    /**
     * An element that acts as filter for other elements.
     * 
     * @param paramElement parameter HTML element being filtered
     * @param filterElement HTML element where the user enter the filter
     */
    /* public */ function FilterElement(paramElement, filterElement) {
        this.paramElement = paramElement;
        this.filterElement = filterElement;
        this.originalArray = new Array();
        
        // push existing values into originalArray array
        if (this.paramElement.tagName == 'SELECT') { // handle SELECTS
            var options = jQuery(paramElement).children().toArray();
            for (var i = 0; i < options.length; ++i) {
                this.originalArray.push(options[i]);
            }
        } else if (paramElement.tagName == 'DIV') { // handle CHECKBOXES
            if (jQuery(paramElement).children().length > 0 && paramElement.children[0].tagName == 'TABLE') {
                var table = paramElement.children[0];
                var tbody = table.children[0];
                
                var trs = tbody.find('tr');
                for (i = 0; i < trs.length ; ++i) {
                    var tds = trs[i].find('td');
                    var inputs = tds[0].find('input');
                    var input = inputs[0];
                    this.originalArray.push(input);
                }
            }
        }
        this.initEventHandler();
    }
    
    /**
     * Gets the parameter HTML element.
     * 
     * @return HTML element
     */
    FilterElement.prototype.getParameterElement = function() {
        return this.paramElement;
    }
    
    /**
     * Gets the filter element.
     * 
     * @return HTML element
     */
    FilterElement.prototype.getFilterElement = function() {
        return this.filterElement;
    }
    
    /**
     * Gets an array with the original options of a filtered element. Useful for recreating the initial setting.
     * 
     * @return <code>Array</code> with HTML elements
     */
    FilterElement.prototype.getOriginalArray = function() {
        return this.originalArray;
    }
    
    FilterElement.prototype.setOriginalArray = function(originalArray) {
    	this.originalArray = originalArray;
    }
    
    /**
     * Initiates an event listener for Key Up events. Depending on the element type it will interpret the filter, and
     * the filtered element, to update its values.
     */
    FilterElement.prototype.initEventHandler = function() {
        var _self = this;
        jQuery(this.filterElement).keyup(function(e) {
            //var filterElement = e.target;
            var filterElement = _self.getFilterElement();
            var filteredElement = _self.getParameterElement();
            
            var text = filterElement.value.toLowerCase();
            var options = _self.originalArray;
            var newOptions = Array();
            for (var i = 0; i < options.length; i++) {
                if (options[i].innerHTML.toLowerCase().match(text)) {
                    newOptions.push(options[i]);
                }
            }
            var tagName = filteredElement.tagName;
            if (tagName == 'SELECT') { // handle SELECT's
               jQuery(filteredElement).children().remove();
               for (var i = 0; i < newOptions.length ; ++i) {
                   var opt = document.createElement('option');
                   opt.value = newOptions[i].value;
                   opt.innerHTML = newOptions[i].innerHTML;
                   jQuery(filteredElement).append(opt);
               }
            } else if (tagName == 'DIV') { // handle CHECKBOXES, RADIOBOXES and other elements (Jenkins renders them as tables)
               if (filteredElement.children().length > 0 && $(filteredElement.children[0]).prop('tagName') == 'TABLE') {
                    var table = filteredElement.children[0];
                    var tbody = table.children[0];
                    
                    trs = tbody.find('tr');
                    for (var i = 0; i < trs.length; i++) {
                        tbody.remove(trs[i]);
                    }
                    
                    var originalArray = [];
                    if (filteredElement.className == 'dynamic_checkbox') {
                        for (var i = 0; i < newOptions.length; i++) {
                            var entry = newOptions[i];
                            // TR
                            var tr = document.createElement('tr');
                            var idValue = 'ecp_' + e.target.paramName + '_' + i;
                            idValue = idValue.replace(' ', '_');
                            tr.attr('id', idValue);
                            tr.attr('style', 'white-space:nowrap');
                            // TD
                            var td = document.createElement('td');
                            // INPUT
                            var input = document.createElement('input');
                            // LABEL
                            var label = document.createElement('label');
                            
                            if (!entry instanceof String) {
                                input.attr('json', JSON.stringify(entry.value));
                                input.attr('name', 'value');
                                input.attr("value", JSON.stringify(entry.value));
                                input.attr("class", " ");
                                input.attr("type", "checkbox");
                                label.addClass("attach-previous");
                                label.innerHTML = JSON.stringify(entry);
                            } else {
                                input.attr('json', entry);
                                input.attr('name', 'value');
                                input.attr("value", entry);
                                input.attr("class", " ");
                                input.attr("type", "checkbox");
                                label.addClass("attach-previous");
                                label.innerHTML = entry;
                            }
                            // Put everything together
                            td.append(input);
                            td.append(label);
                            tr.append(td);
                            tbody.append(tr);
                        }
                    } else {
                        for (var i = 0; i < newOptions.length; i++) {
                            var entry = newOptions[i];
                            // TR
                            var tr = document.createElement('tr');
                            var idValue = 'ecp_' + e.srcElement.paramName + '_' + i;
                            idValue = idValue.replace(' ', '_');
                            tr.attr('id', idValue);
                            tr.attr('style', 'white-space:nowrap');
                            // TD
                            var td = document.createElement('td');
                            // INPUT
                            var input = document.createElement('input');
                            // LABEL
                            var label = document.createElement('label');
                            
                            if (!entry instanceof String) {
                                input.attr('json', JSON.stringify(entry));
                                input.attr('name', 'value');
                                input.attr("value", JSON.stringify(entry));
                                input.attr("class", " ");
                                input.attr("type", "radio");
                                label.addClass("attach-previous");
                                label.innerHTML = JSON.stringify(entry);
                            } else {
                                input.attr('json', entry);
                                input.attr('name', 'value');
                                input.attr("value", entry);
                                input.attr("class", " ");
                                input.attr("type", "radio");
                                label.addClass("attach-previous");
                                label.innerHTML = entry;
                            }
                            // Put everything together
                            td.append(input);
                            td.append(label);
                            tr.append(td);
                            tbody.append(tr);
                        }
                    }
                }
            }
        });
    }
    
    // HTML utility methods
    
    /**
     * <p>Fake selects a radio button.</p>
     * 
     * <p>In Jenkins, parameters in general have two main HTML elements. One which name is name with the value as the 
     * parameter name. And the other which name is value and with the value as the parameter value. For example:</p>
     * 
     * <code>
     * &lt;div name='name' value='parameter1'&gt;
     * &lt;div name='value' value='Sao Paulo'&gt;
     * </code>
     * 
     * <p>This code ensures that only one radio button, in a radio group, contains the name value. Avoiding several 
     * values to be submitted.</p>
     * 
     * @param clazzName HTML element class name
     * @param id HTML unique element id
     * 
     * @see issue #21 in GitHub - github.com/biouno/uno-choice-plugin/issues
     */
     /* public */ function fakeSelectRadioButton(clazzName, id) {
        // deselect all radios with the class=clazzName
        var radios = $('input[class="'+clazzName+'"]');
        radios.each(function(index) {
            $(this).attr('name', '');        
        });
        // select the radio with the id=id
        var radio = $('#' + id);
        if (radio && radio.length > 0)
            radio[0].setAttribute('name', 'value');
    }
    
    /**
     * <p>Gets the value of a HTML element to use it as value in a parameter in Jenkins.</p>
     * 
     * <p>For a HTML element which name is 'value', we use the {@link #getElementValue()} method to retrieve it.</p>
     * 
     * <p>For a DIV, we look for children elements with the name equal to 'value'.</p>
     * 
     * <p>For a input with type equal file, we look for files to use as value.</p>
     * 
     * <p>When there are multiple elements as return value, we append all the values to an Array and return its 
     * value as string (i.e. toString()).</p>
     * 
     * @param htmlParameter HTML element
     * @return <code>String</code> the value of the HTML element used as parameter value in Jenkins, as a string
     */
     /* public */ function getParameterValue(htmlParameter) {
    	var e = htmlParameter.getParameterElement();
    	e = jQuery(e);
        var value = '';
        if (e.attr('name') == 'value') {
            value = getElementValue(e);
        }  else if (e.prop('tagName') == 'DIV') {
            var subElements = e.find('input[name="value"]');
            if (subElements) {
                var valueBuffer = Array();
                subElements.each(function() {
                    var tempValue = getElementValue(jQuery(this));
                    if (tempValue)
                        valueBuffer.push(tempValue);
                });
                value = valueBuffer.toString();
            }
        } else if (e.attr('type') == 'file') {
            var filesList = e.files;
            if (filesList && filesList.length > 0) {
                var firstFile = filesList[0]; // ignoring other files... but we could use it...
                value = firstFile.name;
            } 
        }
        return value;
    }
    
    /**
     * Gets the value of a HTML element as string. If the returned value is an Array it gets serialized first. 
     * Correctly handles SELECT, CHECKBOX, RADIO, and other types. 
     * 
     * @param htmlParameter HTML element
     * @return <code>String</code> the returned value as string. Empty by default.
     */
    function getElementValue(htmlParameter) {
        var value = '';
        var e = jQuery(htmlParameter);
        if (e.prop('tagName') == 'SELECT') {
            value = getSelectValues(e);
        } else if (e.attr('type') == 'checkbox' || e.attr('type') == 'radio') {
            value = (e.attr('checked')== true || e.attr('checked')== 'checked') ? e.attr('value'): '';
        } else {
            value = e.attr('value');
        }
        
        if (value instanceof Array)
            value = value.toString()
        
        return value;
    }
    
    /**
     * Gets an array of the selected option values in a HTML select element.
     * 
     * @param select HTML DOM select element
     * @return <code>Array</code>
     * 
     * @see http://stackoverflow.com/questions/5866169/getting-all-selected-values-of-a-multiple-select-box-when-clicking-on-a-button-u
     */
    function getSelectValues(select) {
        var result = [];
        var options = select && select.children('option:selected');
        for (var i = 0; options != undefined && i < options.length; i++) {
            var option = options[i];
            result.push(option.value || option.text);
        }
        return result;
    }
    
    // Basic utility methods
    
    /**
     * Utility method to check if a text ends with a given pattern.
     * 
     * @param text string
     * @param pattern string
     * @return <code>true</code> iff the string ends with the pattern, <code>false</code> otherwise.
     */
    function endsWith(text, pattern) {
        var d = text.length - pattern.length;
        return d >= 0 && text.lastIndexOf(pattern) === d;
    };
    
    // Hacks in Jenkins core
    
    /**
     * <p>This function is the same as makeStaplerProxy available in Jenkins core, but executes calls 
     * <strong>synchronously</strong>. Since many parameters must be filled only after other parameters have been 
     * updated, calling Jenkins methods assynchronously causes several unpredictable errors.</p>
     * 
     * <p>TODO: example</p>
     */
    /* public */ function makeStaplerProxy2(url, crumb, methods) {
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
                        async: "false", // Here's the juice
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
                        asynchronous: false, // and here
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
    
    // Deciding on what is exported and returning instance
    //instance.endsWith = endsWith;
    instance.fakeSelectRadioButton = fakeSelectRadioButton;
    instance.getParameterValue = getParameterValue;
    instance.CascadeParameter = CascadeParameter;
    instance.ReferencedParameter = ReferencedParameter;
    instance.FilterElement = FilterElement;
    instance.makeStaplerProxy2 = makeStaplerProxy2;
    return instance;
})(jQuery);
