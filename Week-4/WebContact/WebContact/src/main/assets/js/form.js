$(document).ready(function () {

    // Form Validation
    $( "#contactForm").validate({

        rules: {
            nameInput: {
                required: true,
                lettersonly: true
            },
            phoneInput: {
                required: true,
                phoneUS: true
            },
            emailInput: {
                required: true,
                email: true
            },
            addressInput: {
                required: true,
            }
        },
        submitHandler: function(form) {
            var name = $('#nameInput').val();
            var number = $('#phoneInput').val();
            var email = $('#emailInput').val();
            var address = $('#addressInput').val();
            console.log('Name = ' + name + ' number = ' + number);
            console.log('Email = ' + email + ' Address = ' + address);

            Android.saveContact(name,number,email,address);
        }
    });

});