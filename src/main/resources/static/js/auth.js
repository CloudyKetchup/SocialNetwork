function chooseAvatar() {
	$('#choose-profile-photo').click()
}
function chooseBackground() {
	$('#choose-background-photo').click()
}
// change profile photo to choosen one
async function changeAvatar() {
	const reader = new FileReader()

	reader.onload = async (e) => $('.profile-photo').attr('src', e.target.result)

	reader.readAsDataURL($('#choose-profile-photo').prop('files')[0])
}
// change profile background to choosen one
async function changeBackground() {
	const reader = new FileReader()

	reader.onload = async (e) => $('.profile-background').attr('src', e.target.result)

	reader.readAsDataURL($('#choose-background-photo').prop('files')[0])
}
function loginTrigger() {
  const email    = $('.email-field')
  const password = $('.password-field')
  if (email.val() !== '' && password.val() !== '') {
    // login procedure
    login(email,password)
  }
}
// login request
function login(emailfield,passwordfield) {
  $.ajax({
    type : 'POST',
    contentType : 'application/json; charset=utf-8',
    url : 'http://localhost:8080/login',
    data : JSON.stringify({
      'email'	: emailfield.val(),   // email value from field
      'password': passwordfield.val() // password value from field
    }),
    success : (result) => {
	    // check if login was successful
		if(result['response'] === 'login success') {
		    // create cookie with user data : name, surname, email, password
		    setCookie(result['account'])
		    // redirect to home page
		    window.location.replace('/')
		}else if (result['response'] === 'wrong password') {
		    // empty fields
		    emailfield.val('')
		    passwordfield.val('')
		}
    }
  })
}
function registerTrigger() {
  // register form fields
  const name 	 = $('.name-field')
  const surname  = $('.surname-field')
  const email    = $('.email-field')
  const password = $('.password-field')
  
  register(name, surname, email, password)
}
// registration request
function register(name, surname, email, password) {
	$.ajax({
		type : 'POST',
		contentType : 'application/json; charset=utf-8',
		url  : 'http://localhost:8080/register',
		data : JSON.stringify({
			'name'	  : name.val(),
		 	'surname' : surname.val(),
		  	'email'   : email.val(),
	      	'password': password.val()
	   	}),
	    success : (result) => {
		    // check if registration was successful
		    if (result === 'registered') {
		        sendProfilePicture(email)
		        sendBackgroundPicture(email, password)
		    }else {
		        // empty fields if failed
		        name.val('')
		        surname.val('')
		        email.val('')
		        password.val('')
		    }
	    }
  	})
}
function sendProfilePicture(email) {
	const formData = new FormData()
  	formData.append('picture', $('#choose-profile-photo').prop('files')[0])
  	formData.append('email', email.val())
	$.ajax({
		type : 'POST',
		url  : 'http://localhost:8080/images/add/user/profile-picture',
		contentType : false,
    	processData : false,
    	data : formData
	})
}
function sendBackgroundPicture(email, password) {
	const formData = new FormData()
  	formData.append('picture', $('#choose-background-photo').prop('files')[0])
  	formData.append('email', email.val())
	$.ajax({
		type : 'POST',
		url  : 'http://localhost:8080/images/add/user/background-picture',
		contentType : false,
    	processData : false,
    	data : formData,
    	success : () => login(email, password)
	})
}
// create cookie
function setCookie(account) {
  // remove old cookie
  Cookies.remove('account-data',{ path : ''})
  // create new cookie
  Cookies.set('account-data', {
    'id'      : account['id'],
    'name'	  : account['name'],
    'email'   : account['email'],
    'password': account['password'],
    'path'    : ''  // path to cookie
  })
}