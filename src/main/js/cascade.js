// global referencedParameters is an array with the jelly form value

/**
 * Used to extend arrays. Used to check if the array of referenced parameters 
 * contains an element.
 */
Array.prototype.contains = function(obj) {
    var i = this.length;
    while (i--) {
        if (this[i] === obj) {
            return true;
        }
    }
    return false;
}

var refs = Array();

Behaviour.specify('div[name="parameter"]', '', 1, function(e) {
    var hiddenNames = findElementsBySelector(e, 'input[name="name"]', false);
    if (referencedParameters.contains(hiddenNames[0].value)) {
        // here we have the desired element to monitor
        e.onchange = function(e) {
            updateCascadeParameter(e);
        }
        // add the element to an refs array 
        refs.push(e);
    }
});

/** 
 * Updates the cascada parameter using the values from other fields.
 */
updateCascadeParameter = function(e) {
    params = new Array();
    // get all parameter values
    for (var i = 0; i < refs.length ; i++) {
        value = getParameterValue(refs[i]);
        params.push(value);
    }
    s = params.join(',');
    // call the doUpdate method
    // update the select element
}

getParameterValue = function(e) {
    var name = '';
    var value = '';
    var children = e.children;
    for (var i = 0; i < children.length; ++i) {
        child = children[i];
        if (child.nodeName != "INPUT" && child.getAttribute('type') != 'hidden') {
            if (child.nodeName == 'SELECT')
                value = getSelectValues(child);
            else
                value = child.value;
            if (value == '') // multi selects or radios not selected
                value = '';
        } else {
            name = child.value;
        }
    }
    return name + '=' + value;
}

// Return an array of the selected opion values
// select is an HTML select element
// From: http://stackoverflow.com/questions/5866169/getting-all-selected-values-of-a-multiple-select-box-when-clicking-on-a-button-u
function getSelectValues(select) {
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
