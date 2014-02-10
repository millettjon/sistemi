jQuery(document).ready(function() {
    console.log("loading file");
    // Focus the first form element.
    $('#name').focus();

    // Validate form on keyup and submit
    $('#payment-form').validate({
        rules: {
            'name': 'required',
            'address1': 'required',
            'city': 'required',
            'region': 'required',
            'code': 'required',
            'country': 'required'
        }
    });
});
