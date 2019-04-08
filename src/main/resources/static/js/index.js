document.getElementById('create-group').onclick = () => {
  $('#create-group-dialog').css('display','block');
}

document.getElementById('close').onclick = () => {
  $('#create-group-dialog').css('display','none');
}

document.getElementById('create-group-submit').onclick = () => {
  if ($('#group-name').val() != '') {
    createGroup()
  }else {
    $('#field-error')
      .css('display','block')
      .html('Field cannot be empty')
  }
}

document.getElementById('group-name').onkeypress = () => {
  $('#field-error').html('')
}

function getData() {
  if (Cookies.get('account-data') != undefined) {
    dataPOST()
  }
}

function dataPOST() {
  $.ajax({
    type : 'POST',
    contentType : 'application/json; charset=utf-8',
    dataType : 'json',
    url  : 'http://localhost:8080/user_data',
    data : Cookies.get('account-data'),
    success : function(result) {
      console.log(result)
    }
  })
}

function loginTrigger() {
  const email    = $('#login-email')
  const password = $('#login-password')
  login(email,password)
};

function registerTrigger() {
  const username = $('#name-field')
  const email    = $('#register-email')
  const password = $('#register-password')
  $.ajax({
    type : 'POST',
    contentType : 'application/json; charset=utf-8',
    dataType : 'json',
    url : 'http://localhost:8080/register',
    data : JSON.stringify({
      'username': username.val(),
      'email': email.val(),
      'password': password.val()
    }),
    success : function(result) {
    console.log(result)
      const response = result['response']
      if (response == 'registered') {
        login(email,password)
      }else {
        username.val('')
        email.val('')
        password.val('')
      }
    }
  })
}
function login(emailfield,passwordfield) {
  $.ajax({
    type : 'POST',
    contentType : 'application/json; charset=utf-8',
    dataType : 'json',
    url : 'http://localhost:8080/login',
    data : JSON.stringify({
      'email': emailfield.val(),
      'password': passwordfield.val()
    }),
    success : function(result) {
      loginResponseHandler(result,emailfield,passwordfield)
    }
  })
}

function loginResponseHandler(result,emailfield,passwordfield) {
  if(result['response'] == 'login success'){
    user = result['userBody']
    setCookie(user['username'],user['email'],user['password'])
    window.location.replace('/')
  }else if (result['response'] == 'wrong password'){
    emailfield.val('')
    passwordfield.val('')
  }
}

function setCookie(username,email,password) {
  Cookies.remove('account-data',{ path : ''})
  Cookies.set('account-data', {
    'username': username,
    'email': email,
    'password':password,
    'path': ''
  });
}

function createGroup() {
  $.ajax({
    type : 'POST',
    contentType : 'application/json; charset=utf-8',
    dataType : 'json',
    url : 'http://localhost:8080/new_group',
    data : JSON.stringify({
      'name' : $('#group-name').val(),
      'admin': JSON.parse(Cookies.get('account-data'))['email']
    }),
    success : function(result) {
      console.log(result)
    }
  })
}

function findUser() {

}