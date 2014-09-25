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
var UnoChoice = (function($) {
    // The final public object
    var instance = {};
    
    // Plug-in classes
    
    /**
     * A parameter that references parameters.
     */
    /* public */ function CascadeParameter(paramName, paramElement, proxy) {
    	this.paramName = paramName;
    	this.paramElement = paramElement;
    	this.proxy = proxy;
    	this.referencedParameters = [];
    }
    
    CascadeParameter.prototype.getParameterName = function() {
    	return this.paramName;
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
     * @param e HTML element
     * @return <code>String</code> the value of the HTML element used as parameter value in Jenkins, as a string
     */
     /* public */ function getParameterValue(e) {
        var value = '';
        if (e.attr('name') == 'value') {
            value = getElementValue(e);
        }  else if (e.prop('tagName') == 'DIV') {
            var subElements = $(e).find('input[name="value"]');
            if (subElements) {
	            var valueBuffer = Array();
	            subElements.each(function() {
	                var tempValue = getElementValue($(this));
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
     * @param e HTML element
     * @return <code>String</code> the returned value as string. Empty by default.
     */
    function getElementValue(e) {
        var value = '';
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
        var options = select && select.children('option');
        for (var i = 0; options != undefined && i < options.length; i++) {
            var option = options[i];
            if ('selected' === $(option).attr('selected')) {
                result.push(option.value || option.text);
            }
        }
        return result;
    }
    
    // Basic utility methods
    
    /**
     * Utility method to check if a text ends with a given pattern.
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
    function makeStaplerProxy2(url, crumb, methods) {
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
    return instance;
})(jQuery);