feedOpened  = true
groupOpened = false

function onLoad() {
  onloadRedirect()
  newsFeed()
  // $.ajax({
  //   type : 'GET',
  //   url : 'http://localhost:8080/search_group/first group',
  //   success : (result) => {
  //     console.log(result)
  //   }
  // })
}
// return cookie data
function getUser() {
  return JSON.parse(Cookies.get('account-data'))
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
  // check if group name field is not empty and group image was selected
  if ($('#group-name').val() !== '') {
    createGroup()
  }else if ($("#choose-group-image").prop('files')[0] !== undefined){
    $('#field-error')
      .css('display','block')
      .html('Image not selected')
  }else {
    $('#field-error')
      .css('display','block')
      .html('Field cannot be empty')
  }
}
// hide error field if something is typed in group name field
document.getElementById('group-name').onkeypress = () => {
  $('#field-error').html('')
}
function chooseAvatar() {
  $('#choose-user-image').click()
}
// change avatar image on register page after selecting image
function changeAvatar() {
  const reader = new FileReader()

  reader.onload = (e) => {
     $('#avatar').attr('src', e.target.result)
  }
  reader.readAsDataURL($('#choose-user-image').prop('files')[0])
}
// check login status
function onloadRedirect() {
  // check if cookie exist
  if (Cookies.get('account-data') === undefined) {
    window.location.href = "/register"
  }else {
    getUserGroups()
  }
}
// add news feed to main container
function newsFeed() {
  $('.main-container')
    .append(
      $('<div/>')
        .attr('class','feed')
        .append(
          // new post input and submit
          newPostContainer("User",getUser()['id'])
            .css({
              'top': '0px',
              'width': '100%',
              'margin-left': '0'
            }),
          $('<div/>')
            .attr('class','feed-content')
            .append(
              $('<div/>')
                .attr('class','posts')
            )
        )
    )
  getFeedPosts()
}
// get home feed posts
function getFeedPosts() {
  $.ajax({
    type : 'GET',
    url : 'http://localhost:8080/feed_posts/' + getUser()['id'],
    success : (result) => {
      feedPosts = $.extend(true, {}, result)
      renderFeedPosts(result)
    }
  })
}
// render all posts in feed
function renderFeedPosts(posts) {
  $('.posts').empty()
  appendPosts(posts)
}
/* json from ajax give objects in random
  so we sort them by id */
function sortJsonObjects(objects) {
  for (let i = 0; i < objects.length; i++) {
    for(let j = 0; j < objects.length; j++) {
      if (objects[i]['id'] > objects[j]['id']) {
        const temp = objects[i]
        objects[i] = objects[j]
        objects[j] = temp
      }
    }
  }
  return objects
}
function loginTrigger() {
  const email    = $('#login-email')
  const password = $('#login-password')
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
      'email': emailfield.val(),      // email value from field
      'password': passwordfield.val() // password value from field
    }),
    success : (result) => {
      // handle json response after login
      loginResponseHandler(result,emailfield,passwordfield)
    }
  })
}
function registerTrigger() {
  // register form fields
  const username = $('#name-field')
  const email    = $('#register-email')
  const password = $('#register-password')
  if (
    username.val()  !== ''
    &&
    email.val()     !== ''
    &&
    password.val()  !== ''
  ) {
      register(username,email,password)
  }
}
// registration request
function register(username,email,password) {
  const formData = new FormData()
  formData.append('image',$('#choose-user-image').prop('files')[0])
  formData.append('data', JSON.stringify({
      // username value from field
      'username': username.val(),
      // email value from field
      'email'   : email.val(),
      // password value from field
      'password': password.val()
   })
  )
  $.ajax({
    type : 'POST',
    url : 'http://localhost:8080/register',
    contentType: false,
    processData: false,
    data : formData,
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
function loginResponseHandler(result,emailfield,passwordfield) {
  // check if login was successful
  if(result['response'] === 'login success') {
    // create cookie with user data : username, email, password
    setCookie(result['account'])
    // redirect to home page
    window.location.replace('/')
  }else if (result['response'] === 'wrong password') {
    // empty fields
    emailfield.val('')
    passwordfield.val('')
  }
}
// create cookie
function setCookie(account) {
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
// create new group request
function createGroup() {
  const formData = new FormData()
  // image file
  formData.append('image', $("#choose-group-image").prop('files')[0])
  // background file
  formData.append('background', $("#choose-group-background").prop('files')[0])
  // group name
  formData.append('name' , $('#group-name').val())
  // group admin email
  formData.append('admin', getUser()['email'])
  $.ajax({
    type : 'POST',
    url : 'http://localhost:8080/new_group',
    contentType: false,
    processData: false,
    data : formData,
    success : (result) => {
      // check if group was created
      if (result === 'group created') {
        // clear groups list
        $('.groups-box').empty()
        // update user groups
        getUserGroups()
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
// get group data like posts,images,followers...
function getGroupData(group) {
  $.ajax({
    type : 'GET',
    async : false,
    url : 'http://localhost:8080/get_group/' + group['id'],
    success : (result) => {
      groupData = $.extend(true, {}, result)
    }
  })
}
// request user groups json
function getUserGroups() {
  $.ajax({
    type : 'POST',
    contentType : 'application/json; charset=utf-8',
    url  : 'http://localhost:8080/user_groups',
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
// add group to groups list at right panel
function renderGroups(group) {
  // append group div to group list
  $('.groups-box').append(
    $('<button/>')
      .html(group['name'])
      .attr('class','group')
      .click(() => {
        $('.group-container').remove()
        getGroupData(group)
        // render group photo,posts and followers
        renderGroupContainer()
      }
    )
  )
}
function renderGroupContainer() {
  // remove news feed
  $('.feed').remove()
  feedOpened  = false
  groupOpened = true
  // hide right panel containing friends
  $('.right-panel').css('display','none')
  // add group container
  $('.main-container').append(
    $('<div/>')
      .attr('class','group-container')
      // append group container elements
      .append(
        // group header
        $('<div/>')
          .attr('class','group-header')
          .css(
            'background-image',
            'url(' + 'http://localhost:8080/group/background/' + groupData['backgroundPhoto']['id'] + ')'
          )
          .append(
            // overlay making group background image darker
            $('<div/>')
              .attr('class','group-header-overlay'),
            // close button
            groupContainerClose(),
            // group follow button
            groupFollow(),
            // container with new post input/submit
            newPostContainer("Group",groupData['id']),
            $('<p/>')
              .html(groupData['followers'].length + '  Followers'),
            // container storing group images
            groupContainerImage(),
          ),
        $('<div/>')
          .attr('class','group-content')
          .append(
            $('<div/>').attr('class','posts')
          )
      ),
    groupfollowers(groupData['followers'])
  )
  appendPosts(groupData['posts'])
}
// close button for container
function groupContainerClose() {
  return $('<span/>')
    .attr('id','close')
    .append('<i/>')
      .attr('class','fas fa-chevron-left')
      .click(() => {
          // show right panel containing friends
          $('.right-panel').css('display','block')
          // remove followers container
          $('.group-followers-container').remove()
          // remove group container
          $('.group-container').remove()
          feedOpened  = false
          groupOpened = true
          // restore news feed
          newsFeed()
        }
      )
}
// new post container
function newPostContainer(postType,parent_id) {
  return $('<div/>')
    .attr('class','new-post')
    // add elements to new post container
    .append([
      // post content field
      $('<input/>')
        .attr({
          'class': 'new-post-input',
          'placeholder': 'Post to wall'
        }),
      // choose picture for post button
      $('<input/>')
        .attr({
          'class': 'choose-post-picture',
          'type' : 'file'
        }),
      // icon for choose post picture
      $('<label/>')
        .attr({
          'for': 'choose-post-picture',
          'class': 'choose-post-picture-icon'
        })
        // image icon on label
        .append($('<i/>').attr('class','fas fa-image'))
        // on click simulate click on choose post picture input>
        .click(() => $('.choose-post-picture').click()),
      // post submit button
      $('<button/>')
        .attr('class','new-post-submit')
        .append(
          $('<i/>')
            .attr('class','fa fa-paper-plane')
        )
        .click(() => sendNewPost(postType,parent_id))
    ])
}
// send new post to group or user wall
function sendNewPost(postType,parent_id) {
  const postPicture = $('.choose-post-picture').prop('files')[0]
  $.ajax({
    type : 'POST',
    contentType : 'application/json; charset=utf-8',
    url : 'http://localhost:8080/new_post',
    data : JSON.stringify({
      // post text
      'content' : $('.new-post-input').val(),
      // post author
      'author' : getUser()['id'],
      // time when post is created
      'time' : new Date().getTime(),
      // id of entity where to send post group or user
      'for' : parent_id,
      // group or user post
      'post_type' : postType
    }),
    success : (result) => {
      // empty post input
      $('.new-post-input').val('')
      // check if post have picture
      if (postPicture !== undefined) {
        sendPostPicture(result,postPicture)
      }else {
        // check if post was send to user or group
        if (result['type'] === "User") {
          // update feed posts
          getFeedPosts()
        }else {
          // update group data
          getGroupData(groupData)
          updatePosts(groupData['posts'])
        }
      }
    }
  })
}
// add picture to post after post was send
function sendPostPicture(post,postPicture) {
  const formData = new FormData()
  // picture file
  formData.append('post_picture', postPicture)
  // post data
  formData.append('post',JSON.stringify(post))
  $.ajax({
    type : 'POST',
    url : 'http://localhost:8080/add_post_picture',
    contentType : false,
    processData : false,
    data : formData,
    success : (result) => {
      if (post['type'] === "User") {
        // update feed posts
        getFeedPosts()
      }else {
        // update group data
        getGroupData(groupData)
        updatePosts(groupData['posts'])
      }
    }
  })
}
// group image in container in top right corner
function groupContainerImage() {
  // top right container that stores group image
  return imageContainer = $('<div/>')
    .attr('class','group-image-container')
    // container elements
    .append(
      // group image
      $('<img/>')
        .attr({
          'src': 'http://localhost:8080/group/image/' + groupData['profilePhoto']['id'],
          'class': 'group-image'
        }),
      $('<span/>')
        .html(groupData['name'])
    )
}
function groupFollow() {
  // group follow button text
  let followText = 'Follow'
  // user email from cookie
  const email    = getUser()['email']
  // check if user is following group and change follow button text
  for (i in groupData['followers']) {
    // check if user is in group followers list
    if (groupData['followers'][i]['email'] === email){
      // change follow button text
      followText = 'Following'
      break
    }
  }
  // group follow button
  return $('<button/>')
    .attr('class','group-follow')
    .html(followText)
}
// container with group followers at bottom left
function groupfollowers(followers) {
  const followersContainer = $('<div/>')
    .attr('class','group-followers-container')
    .append(
      $('<div/>')
        .attr('class','group-followers')
    )
  // add all followers from group to container
  for (i in followers) {
    followersContainer
      .find('.group-followers')
      .append(groupfollower(followers[i]))
  }
  return followersContainer
}
function groupfollower(follower) {
  return $('<div>')
    .attr('class','group-follower')
    .append([
      $('<div/>')
        .attr('class','group-follower-image')
        .append(
          $('<img/>')
            .attr('src','http://localhost:8080/user/profile_picture/' + follower['email'])
        ),
      $('<span/>')
        .attr('class','group-follower-name')
        .html(follower['username'])
    ])
}
function getPostAuthor(post_id) {
  let author
  $.ajax({
    type : 'GET',
    url : 'http://localhost:8080/post_author/' + post_id,
    async : false,
    success : (result) => {
      author = result
    }
  })
  return author
}
// add all posts to posts container
function appendPosts(posts) {
  // sort posts
  const sortedPosts = sortJsonObjects(posts)
  // append to container all posts
  for (i in sortedPosts) {
    const post = sortedPosts[i]
    // add post to feed
    $('.posts').append(postDiv(post))
    // if post contains picture add img element
    if (post['picture'] !== null) {
      $('.post-' + post['id'] + '-content-box')
        .append(
          // picture
          $('<img/>')
            .attr({
              'class': 'post-picture',
              'src': 'http://localhost:8080/post/picture/' + post['id']
            })
        )  
    }
  }
}
function postDiv(post) {
  const postDiv = $('<div/>')
    .attr({
      'class': 'post',
      'id': 'post-' + post['id']
    })
    .append(
      // post header
      $('<div/>')
        .attr('class','post-header')
        .append(
          // post author image and name
          postAuthor(post['id']),
          // time when post was created
          $('<span/>')
            .attr('class','post-time')
            .html(timeConverter(post['time']))
        ),
      // content text
      $('<div/>')
        .attr('class','post-' + post['id'] + '-content-box')
        .append(
          $('<p/>')
            .attr('class','post-text')
            .html(post['content']),
          $('<div/>')
            .attr('class','post-comments')
        ),
      // post footer
      $('<div/>')
        .attr('class','post-footer')
        .css('margin-bottom','-10px')
        .append(
          likeButton(post),
          commentButton(post)
        )
    )
    if (post['picture'] !== null) {
      postDiv.append(commentsContainer(post))
    }
    return postDiv
}
// convert time from long to hour and minute format
function timeConverter(time) {
    return new Date(parseInt(time))
      .toLocaleTimeString()
      .replace(/:\d+ /, ' ');
}
// post author image and username
function postAuthor(post_id) {
  author = getPostAuthor(post_id)
  return [
    // username
    $('<span/>')
      .html(author['username'])
      .css({
        'line-height':'200%',
        'margin-left':'10px'
      }),
    // author image
    $('<div/>')
      .attr('class','post-author-image')
      .append(
        $('<img/>')
          .attr(
            'src','http://localhost:8080/user/profile_picture/' + author['email']
          )
      )
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
        // likes count number on icon
        .html(' ' + post['likes'].length)
    )
  // like button settings based on liked status
  if (liked) {
    likeButton
      // change button color to red
      .css('color','#F44256')
      // on tap will remove user like
      .click(() => sendLike(post,'remove_like'))
  }else {
    likeButton
      // change color to grey
      .css('color','grey')
      // on tap will add user like
      .click(() => sendLike(post,'add_like'))
  }
  return likeButton
}
// send group post like with action add/remove
function sendLike(post,action) {
  $.ajax({
    type : 'POST',
    contentType : 'application/json; charset=utf-8',
    url : 'http://localhost:8080/' + action,
    data : JSON.stringify({
      'post_id'   : post['id'],
      'author_id' : getUser()['id']
    }),
    success: () => {
      // update posts list
      if (feedOpened) {
        getFeedPosts()
      }else if (groupOpened){
        getGroupData(groupData)
        updatePosts(groupData['posts'])
      }
    }
  })
}
// post comments button
function commentButton(post) {
  // comments button
  return $('<button/>')
    .attr('class','comment-button')
    .append(
      $('<i/>')
        .attr('class','fas fa-comment')
        // comments count number on icon
        .html(' ' + post['comments'].length)
    )
}
// container containing comments
function commentsContainer(post) {
  return $('<div/>')
    .attr({
      'class': 'comments-container',
      'id': 'comments-container-' + post['id']
    })
    // comments container elements
    .append(
      $('<p/>')
        .attr('class','comments-paragraph')
        .html('Comments'),
      // comments divs
      postComments(post),
      $('<div/>')
        .append(
          // new comment input
          $('<input/>')
            .attr({
              'class':'comment-input',
              'placeholder':'Comment'
            }),
          // send comment button
          $('<button/>')
            .attr('class','comment-submit')
            .html('Send')
            .click(() =>
              sendComment(post)
            )
        )
    )
}
// comments divs
function postComments(post) {
  const commentsBox = $('<div/>')
    .attr('class','comments')
    .css({
      'overflow': 'auto',
      'height': '205px'
    })
  const sortedComments = sortJsonObjects(post['comments'])
  // add all comments divs
  for (i in sortedComments) {
    commentsBox.append(
      // comment div
      $('<div/>')
        .attr('class','comment')
        // comment elements
        .append(
          // author
          postAuthor(sortedComments[i]['id']),
          // content
          $('<p/>')
            .css({
              'margin-top':'5px',
              'overflow-wrap':'break-word'
            })
            .html(sortedComments[i]['content']),
        )
    )
  }
  return commentsBox
}
// send post comment via request
function sendComment(post) {
  $.ajax({
    type : 'POST',
    contentType : 'application/json; charset=utf-8',
    url : 'http://localhost:8080/new_comment',
    data : JSON.stringify({
      'post_id' : post['id'],
      'content' : $('.comment-input').val(),
      'author'  : getUser()['email']
    }),
    success: (result) => {
      // update post comments
      updateComments(result)
    }
  })
}
// update posts in group container
function updatePosts(posts) {
  // sort posts
  const sortedPosts = sortJsonObjects(posts)
  // remove posts
  $('.posts').empty()
  // append to container new posts
  for (i in sortedPosts) {
    // append post div
    $('.posts').append(postDiv(sortedPosts[i]))
  }
}
// update post comments
function updateComments(post) {
  // remove comments
  $('#comments-container-' + post['id']).remove()
  // update comments container
  $('#post-' + post['id']).append(commentsContainer(post))
}