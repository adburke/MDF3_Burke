$( "#addContactBtn").on("click", function() {
    console.log( "Add Contact Clicked" );
    var name = $('#nameInput').val();
    var number = $('#phoneInput').val();
    var email = $('#emailInput').val();
    var address = $('#addressInput').val();
    console.log('Name = ' + name + ' number = ' + number);
    console.log('Email = ' + email + ' Address = ' + address);

    Android.saveContact(name,number,email,address);
});