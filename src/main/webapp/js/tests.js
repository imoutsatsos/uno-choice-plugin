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
 * @author Bruno P. Kinoshita <brunodepaulak@yahoo.com.br>
 */

/**
 * Initial set up.
 */
QUnit.test("Tests that UnoChoice module was loaded", function (assert) {
	assert.ok(UnoChoice !== null, "UnoChoice object exists");
});

/**
 * Tests for the endsWith function.
 */
QUnit.test("Test endsWith", function(assert) {
	assert.ok(UnoChoice.endsWith("Bruno", "uno"), "endsWith works as expected");
});

/**
 * Tests for the fakeSelectRadioButton function.
 */
QUnit.test("Test fakeSelectRadioButton", function(assert) {
	var $fixture = $("#qunit-fixture");
	$fixture.append("<input type='radio' name='group1' class='myClazz' id='option1' />");
	$fixture.append("<input type='radio' name='group1' class='myClazz' id='option2' />");
	$fixture.append("<input type='radio' name='group1' class='myClazz' id='option3' />");
	UnoChoice.fakeSelectRadioButton("myClazz", "option2");
	assert.equal("", $("#option1").attr('name'), "option1 is empty");
	assert.equal("value", $("#option2").attr('name'), "option2 is not empty");
	assert.equal("", $("#option3").attr('name'), "option3 is empty");
});

/**
 * Tests for the getSelectValues function.
 */
QUnit.test("Test getSelectValues", function(assert) {
	var $fixture = $("#qunit-fixture");
	$fixture.append("<select name='select1'><option value='1' selected>1</option><option value='2'>2</option><option value='3' selected>3</option></select>");
	var select = $("select");
	var arr = UnoChoice.getSelectValues(select);
	assert.ok(arr.length == 2, "array length is correct");
	assert.deepEqual(arr, ['1', '3'], 'array content is correct');
});

/**
 * Tests for the getSelectValues function.
 */
QUnit.test("Test getElementValue", function(assert) {
	var $fixture = $("#qunit-fixture");
	
	$fixture.append("<select name='select1'><option value='1' selected>1</option><option value='2'>2</option><option value='3' selected>3</option></select>");
	var select = $("select");
	var arr = UnoChoice.getElementValue(select);
	assert.equal(arr, '1,3', 'element value is correct for select');
	
	$fixture.empty();
	$fixture.append("<input id='checkbox1' type='checkbox' name='parameter1' checked='checked' value='123' />");
	var e = $("#checkbox1");
	var val = UnoChoice.getElementValue(e);
	assert.equal(val, '123', 'element value is correct for checkbox');
	
	$fixture.empty();
	$fixture.append("<input id='input1' type='text' name='parameter1' checked='checked' value='1234' />");
	var e = $("#input1");
	var val = UnoChoice.getElementValue(e);
	assert.equal(val, '1234', 'element value is correct for text');
	
	$fixture.empty();
	$fixture.append("<select name='select1'></select>");
	var select = $("select");
	var val = UnoChoice.getElementValue(select);
	assert.equal(val, '', 'element value is correct for empty select');
});



