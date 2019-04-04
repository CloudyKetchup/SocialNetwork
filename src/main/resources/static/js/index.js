document.getElementById('submit-login').onclick = () => {
	$.ajax({
		type : "POST",
        contentType : 'application/json; charset=utf-8',
        dataType : 'json',
        url : "http://localhost:8080/login",
        data : JSON.stringify({
        	'email': $('#login-email').val(),
        	'password': $('#login-password').val()
        }),
        success : function(result) {
            console.log(result);
        }
	})
};
document.getElementById('submit-register').onclick = () => {
	$.ajax({
		type : "POST",
        contentType : 'application/json; charset=utf-8',
        dataType : 'json',
        url : "http://localhost:8080/register",
        data : JSON.stringify({
        	'username': $('#name-field').val(),
        	'email': $('#register-email').val(),
        	'password': $('#register-password').val()
        }),
        success : function(result) {
            console.log(result);
        }
	})
};