function onLoad() {
  onloadRedirect()
}

// spawn create room dialog
document.getElementById('create-group').onclick = () => {
  $('#create-group-dialog').css('display','block');
}
// close dialog window
document.getElementById('close').onclick = () => {
  $('#create-group-dialog').css('display','none');
}
// action for submit button in create group dialog
document.getElementById('create-group-submit').onclick = () => {
  if ($('#group-name').val() !== '') {
    // send group image first
    newGroupImage("group_image")
  }else {
    // if name field is empty show error 
    $('#field-error')
      .css('display','block')
      .html('Field cannot be empty')
  }
}
// hide error field if something is typed in group name field
document.getElementById('group-name').onkeypress = () => {
  $('#field-error').html('')
}

// check login status
function onloadRedirect() {
  // check if cookie exist
  if (Cookies.get('account-data') === undefined) {
    window.location.href = "/auth"
  }else {
    getUserData()
  }
}
/* json from ajax give objects in random 
  so we sort them with bubble sort */
function sortJsonObjects(objects) {
  for (let i  = 0; i < objects.length; i++) {
    for(let j = 0; j < objects.length - i - 1; j++){
    if (objects[j]['id'] > objects[j + 1]['id']) {
      let temp   = objects[j]
      objects[j] = objects[j + 1]
      objects[j + 1] = temp
    }
   }
  }
  return objects
}
// request user data json
function getUserData() {
  // ajax post request
  $.ajax({
    type : 'POST',
    contentType : 'application/json; charset=utf-8',
    url  : 'http://localhost:8080/user_data',
    async: false,
    data : Cookies.get('account-data'), // cookie with user data
    success : (result) => {
      // user groups list from request
      const sortedGroups = sortJsonObjects(result['groups'])
      for (i in sortedGroups) {
        // add group div to list from right panel
        renderGroups(sortedGroups[i])
      }
    }
  })
}
// login request
function loginTrigger() {
  const email    = $('#login-email')
  const password = $('#login-password')
  // login procedure
  login(email,password)
};
// register request
function registerTrigger() {
  // register form fields
  const username = $('#name-field')
  const email    = $('#register-email')
  const password = $('#register-password')
  // ajax post request
  $.ajax({
    type : 'POST',
    contentType : 'application/json; charset=utf-8',
    url : 'http://localhost:8080/register',
    data : JSON.stringify({
      'username': username.val(),   // username value from field
      'email': email.val(),         // email value from field
      'password': password.val()    // password value from field
    }),
    success : (result) => {
      // check if registration was successful
      if (result['response'] === 'registered') {
        // login to new account if registered
        login(email,password)
      }else {
        // empty fields if failed
        username.val('')
        email.val('')
        password.val('')
      }
    }
  })
}
// login request
function login(emailfield,passwordfield) {
  // ajax post request
  $.ajax({
    type : 'POST',
    contentType : 'application/json; charset=utf-8',
    url : 'http://localhost:8080/login',
    data : JSON.stringify({
      'email': emailfield.val(),      // email value from field
      'password': passwordfield.val() // password value from field
    }),
    success : (result) => {
      // handle json response after login
      loginResponseHandler(result,emailfield,passwordfield)
    }
  })
}
function loginResponseHandler(result,emailfield,passwordfield) {
  // check if login was successful
  if(result['response'] === 'login success'){
    // create cookie with user data : username, email, password
    setCookie(result['account'])
    // redirect to home page
    window.location.replace('/')
  }else if (result['response'] === 'wrong password'){
    // empty fields
    emailfield.val('')
    passwordfield.val('')
  }
}
// create cookie
function setCookie(account) {
  console.log(account)
  // remove old cookie
  Cookies.remove('account-data',{ path : ''})
  // create new cookie
  Cookies.set('account-data', {
    'id': account['id'],
    'username': account['username'],
    'email': account['email'],
    'password': account['password'],
    'path': ''  // path to cookie
  });
}
// new group request
function createGroup(imagePath) {
  // ajax post request
  $.ajax({
    type : 'POST',
    url : 'http://localhost:8080/new_group',
    contentType : 'application/json; charset=utf-8',
    data: JSON.stringify({
      // name value from group dialog form
      'name': $('#group-name').val(),
      // user email from cookie
      'admin': getUser()['email'],
      // path to group image
      'imagepath': imagePath 
    }),
    success : (result) => {
      // check if group was created
      if (result['response'] === 'group created') {
        // send group image
        // clear groups list
        $('.groups-box').empty()
        // update user data
        getUserData()
      }else {
        // empty name field
        $('#group-name').val('')
        // show field error
        $('#field-error')
          .css('display','block')
          .html('group already exist')
      }
    }
  })
}
// upload image to group
function newGroupImage(action) {
  const formData = new FormData()
  formData.append('image',$("#choose-group-image").prop('files')[0])
  $.ajax({
    type : 'POST',
    url : 'http://localhost:8080/' + action,
    contentType: false,
    processData: false,
    cache: false,
    data : formData,
    success : (result) => {
      // check if image was saved succesfull
      if (result['response'] === 'image saved') {
        // after image uploaded create group 
        createGroup(result['imagepath'])
      }else {
        // show field error
        $('#field-error')
          .css('display','block')
          .html('error occurred')
      }
    }
  })
}
// get group data like posts,images,members...
function getGroupData(group) {
  // ajax post request
  $.ajax({
    type : 'POST',
    contentType : 'application/json; charset=utf-8',
    async : false,
    url : 'http://localhost:8080/group_data',
    data : JSON.stringify({
      'id': group['id']
    }),
    success : (result) => {
      groupData = $.extend(true, {}, result['group'])
    }
  })
}
// return cookie data
function getUser() {
  return JSON.parse(Cookies.get('account-data'))
}
// add group to groups list at right panel
function renderGroups(group) {
  // append group div to group list
  $('.groups-box').append(
    $('<button/>')
      .html(group['name'])
      .attr('class','group')
      .click(() => {
        getGroupData(group)
        renderGroupContainer(group)
      }
    )
  )
}
function renderGroupContainer(group) {
  $('.main-container').append(
    $('<div/>')
      .attr('class','group-container')
      // append group container elements
      .append(
        [
          // close button
          groupContainerClose(group),
          // container with new post input/submit
          newPostContainer(),
          // div containing posts
          groupPostsContainer(groupData['posts']),
          // container stores group image and follow button
          groupContainerImage(groupData['members']),
          // container stores group members list
          groupMembers(groupData['members'])
        ]
      )
  )
}
// close button for container
function groupContainerClose() {
  return $('<span/>')
    .attr('id','close')
    .append('<i/>')
      .attr('class','fas fa-times-circle')
      .click(() =>
        // remove group container from main container
        $('.group-container').remove()
  )
}
// send new post to group via request
function newPost() {
  const contentInput = $('.new-post-input')
  // ajax post request
  $.ajax({
    type : 'POST',
    contentType : 'application/json; charset=utf-8',
    url : 'http://localhost:8080/new_post',
    data : JSON.stringify({
      // post text
      'content' : contentInput.val(),
      // post author
      'author' : getUser()['email'],
      // time when post is created
      'time' : new Date().getTime(),
      // id of group where to send post
      'group_id': groupData['id']
    }),
    success : (result) => {
      contentInput.val('')
      updatePosts()
    }
  })
}
// group new post container
function newPostContainer() {
  return $('<div/>')
    .attr('class','new-post')
    // add elements to new post container
    .append(
      [
        // post content field
        $('<input/>')
          .attr(
            {
              'class': 'new-post-input',
              'placeholder': 'Post to group wall'
            }
          ),
        // post submit button
        $('<button/>')
          .attr('class','new-post-submit')
          .html('Post')
          .click(() => newPost())
      ]
    )
}
function groupPostsContainer(posts) {
  // group posts container
  const groupPosts = $('<div/>')
    .attr('class','group-posts')
  // sort posts
  const sortedPosts = sortJsonObjects(posts)
  // append to container all posts
  for (post in sortedPosts) {
    // append post div
    groupPosts.append(groupPost(sortedPosts[post]))
  }
  return groupPosts
}
// group post container
function groupPost(post) {
  return $('<div/>')
    .attr('class','group-post')
    .append(
      [
        $('<div/>')
          .attr('class','group-post-header')
          .append(postAuthor(post['author'])),
        // content text
        $('<div/>')
          .attr('class','post-content-box')
          .append(
            $('<p/>')
              .html(post['content'])
          ),
        $('<div/>')
          .attr('class','group-post-footer')
          .append(likeButton(post))
      ]
    )
}
// post author image and username
function postAuthor(author) {
  return [
    // username
    $('<span/>')
      .html(author['username'])
      .css('line-height','250%'),
    // author image
    $('<div/>')
      .attr('class','group-member-image')
      .append('<img/>')
  ]
}
// like button with heart icon
function likeButton(post) {
  // user liked this post true/false
  let liked
  // check if user already liked post
  if (post['likes'].includes(getUser()['id'])) {
    liked = true
  }else {
    liked = false
  }
  const likeButton = $('<button/>')
    .attr('class','like-button')
    .append(
      $('<i/>')
        .attr('class','fas fa-heart')
        .html(' ' + post['likes'].length)
    )
  // like button settings based on liked status
  if (liked) {
    likeButton
      // change color to red
      .css('color','#F44256')
      // on tap will remove user like
      .click(() => sendLike(post, 'remove_like'))
  }else {
    likeButton
      // change color to grey
      .css('color','grey')
      // on tap will add user like
      .click(() => sendLike(post, 'add_like'))
  }
  return likeButton
}
// send group post like with action add/remove
function sendLike(post,action) {
  // ajax post request
  $.ajax({
    type : 'POST',
    contentType : 'application/json; charset=utf-8',
    url : 'http://localhost:8080/' + action,
    async: false,
    data : JSON.stringify({
      'group_id' : groupData['id'],
      'post_id'  : post['id'],
      'user_id'  : getUser()['id']
    }),
    success: (result) => {
      updatePosts()
    }
  })
}
// group image in container in top right corner
function groupContainerImage(members) {
  // group follow button text
  let followText  = 'Follow'
  // user email from cookie
  const email     = getUser()['email']
  // check if user is following group and change follow button text
  for (i in members) {
    // check if user is in group members list
    if (members[i]['email'] === email){
      // change follow button text
      followText  = 'Following'
      break
    }
  }
  // top right container that stores group image and follow button
  return imageContainer = $('<div/>')
    .attr('class','group-image-container')
    // container elements
    .append(
      [
        // group image
        $('<div/>')
          .attr('class','group-image')
          .append('<img/>'),
        // group follow button
        $('<button/>')
          .attr('class','group-follow')
          .html(followText)
      ]
    )
}
// container with group members at bottom left
function groupMembers(members) {
  const membersContainer = $('<div/>')
    .attr('class','group-members-container')
    .append(
      $('<div/>')
        .attr('class','group-members')
    )
  // add all members from group to container
  for (i in members) {
    membersContainer
      .find('.group-members')
      .append(groupMember(members[i]))
  }
  return membersContainer
}
function groupMember(member) {
  return $('<div>')
    .attr('class','group-member')
    .append(
      [
        $('<div/>')
          .attr('class','group-member-image')
          .append('<img/>'),
        $('<span/>')
          .attr('class','group-member-name')
          .html(member['username'])
      ]
    )
}
// update posts in group container
function updatePosts() {
  // remove posts
  $('.group-posts').remove()
  // update group data
  getGroupData(groupData)
  // append to group container new group
  $('.group-container').append(
    groupPostsContainer(groupData['posts'])
  )
}