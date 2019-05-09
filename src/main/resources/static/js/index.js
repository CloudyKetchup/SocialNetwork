feedOpened   = true
groupOpened  = false
userOpened   = false
searchOpened = false

function onLoad() {
  onloadRedirect()
  newsFeed()
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
// create cookie
function setCookie(account) {
  // remove old cookie
  Cookies.remove('account-data',{ path : ''})
  // create new cookie
  Cookies.set('account-data', {
    'id'      : account['id'],
    'username': account['username'],
    'email'   : account['email'],
    'password': account['password'],
    'path'    : ''  // path to cookie
  })
}
// add search field to navigation
function showSearchField() {
  searchUsers  = true
  searchGroups = false
  $('.nav-wrapper')
    .append(
      $('<input/>')
        .attr({
          'class': 'search search-field',
          'placeholder': 'Search users or Groups'
        })
        .on('keyup', () => {
          if ($('.search-field').val() !== '') {
            // search user or group in database when user is typing
            if (searchUsers) {
              searchEntity('user')
            }else {
              searchEntity('group')
            }
          }
        }),
      // result div
      searchResult()
    )
  changeSearchColors()
  searchOpened = true
}
function closeSearchField() {
  $('.search').remove()
  searchOpened = false
}
function changeSearchColors() {
  if (searchUsers) {
    $('.search-user')
      .css('color','#f44242')
    $('.search-group')
      .css('color','white')
  }else {
    $('.search-group')
      .css('color','#f44242')
    $('.search-user')
      .css('color','white')
  }
}
// container below search
function searchResult() {
  return $('<div/>')
    .attr('class','search search-result')
    .append(
      // choose for search user
      $('<div/>')
        .attr('class','search-user search-method-button')
        .css('float','left')
        .html('User')
        .append(
          $('<i/>')
            .attr('class','fas fa-user')
        )
        .click(() => {
          searchUsers  = true
          searchGroups = false
          changeSearchColors()
        }),
      // choose for search group
      $('<div/>')
        .attr('class','search-group search-method-button')
        .css('float','right')
        .html('Group')
        .append(
          $('<i/>')
            .attr('class','fas fa-users')
        )
        .click(() => {
          searchUsers  = false
          searchGroups = true
          changeSearchColors()
        })
    )
}
// search user or group in database
function searchEntity(entity) {
  $.ajax({
    type  : 'GET',
    url   : 'http://localhost:8080/' + entity + '/name=' + $('.search-field').val(),
    success : (result) => {
      // check if entity finded and div with entity is not already created
      if (result !== '' && $('#search-entity-' + result['id']).length == 0) {
        // append to search result finded user or group
        $('.search-result').append(searchResultEntity(result,entity))
        // save finded entity
        findedEntity = $.extend(true, {} ,result)
      // check if any entity was founded 
      }else if (result === '' && findedEntity !== undefined) {
        // remove previously finded entity div
        $('#search-entity-' + findedEntity['id']).remove()
      }
    }
  })
}
// div for searched user or group
function searchResultEntity(entity,type) {
  return $('<div>')
    .attr({
      'class': 'search-entity',
      'id': 'search-entity-' + entity['id']
    })
    .append([
      $('<div/>')
        .attr('class','search-entity-image')
        .append(
          $('<img/>')
            .attr('src','http://localhost:8080/image/' + entity['profilePicture']['id'])
        ),
      $('<span/>')
        .attr('class','search-entity-name')
        .html(entity['name'])
    ])
    // get searched user data
    .click(() => $.getJSON('http://localhost:8080/user/' + entity['id'], (result) => {
      // show user container

    }))
}
// check login status
async function onloadRedirect() {
  // check if cookie exist
  if (Cookies.get('account-data') === undefined) {
    window.location.href = "/login"
  }else {
    getUserGroups()
  }
}
// add news feed to main container
async function newsFeed() {
  $('.main-container')
    .append(
      $('<div/>')
        .attr('class','feed')
        .append(
          // new post input and submit
          newPostContainer("User")
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
  $.getJSON('http://localhost:8080/user/feed/' + getUser()['id'], (result) => {
    appendPosts(result)
  })
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
    url : 'http://localhost:8080/group/new',
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
    url  : 'http://localhost:8080/group/id=' + group['id'],
    async: false, 
    success : (result) => {
      groupData = $.extend(true, {}, result)
    }
  })
}
function getUserData() {
  $.getJSON('http://localhost:8080/user/name=' + getUser()['name'], (result) => {
    userData = $.extend(true, {}, result)
  })
}
// request user groups json
function getUserGroups() {
  $.getJSON('http://localhost:8080/user/groups/' + getUser()['email'], (result) => {
      // user groups list from request
      const sortedGroups = sortJsonObjects(result)
      for (i in sortedGroups) {
        // add group div to list from right panel
        renderGroups(sortedGroups[i])
      }
  })
}
function renderUserContainer() {
  // remove news feed
  $('.feed').remove()
  feedOpened  = false
  groupOpened = false
  userOpened  = true
  // hide right panel containing friends
  $('.right-panel').css('display','none')
  // add group container
  $('.main-container').append(
    $('<div/>')
      .attr('class','user-container')
      // append group container elements
      .append(
        // group header
        $('<div/>')
          .attr('class','user-header')
          .css(
            'background-image',
            'url(' + 'http://localhost:8080/image/' + userData['backgroundPhoto']['id'] + ')'
          )
          .append(
            // overlay making group background image darker
            $('<div/>')
              .attr('class','user-header-overlay'),
            // close button
            containerClose(),
            // group follow button
            followButton(userData),
            $('<p/>')
              .html(userData['followers'].length + '  Followers'),
            // container storing profile image
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
// add group to groups list at right panel
async function renderGroups(group) {
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
async function renderGroupContainer() {
  // remove news feed
  $('.feed').remove()
  feedOpened  = false
  groupOpened = true
  userOpened  = false
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
            'url(' + 'http://localhost:8080/image/' + groupData['backgroundPhoto']['id'] + ')'
          )
          .append(
            // overlay making group background image darker
            $('<div/>')
              .attr('class','group-header-overlay'),
            // close button
            containerClose(),
            followButton(groupData),
            // container with new post input/submit
            newPostContainer("Group"),
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
function containerClose() {
  return $('<span/>')
    .attr('id','close')
    .append('<i/>')
      .attr('class','fas fa-chevron-left')
      .click(() => {
          // show right panel containing friends
          $('.right-panel').show()
          // remove followers conta iner
          $('.followers-container').remove()
          // remove group container
          $('.group-container').remove()
          feedOpened  = true
          groupOpened = false
          userOpened  = false
          // restore news feed
          newsFeed()
        }
      )
}
// new post container
function newPostContainer(postType) {
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
        .click(() => sendNewPost(postType))
    ])
}
// send new post to group or user wall
function sendNewPost(postType) {
  const postPicture = $('.choose-post-picture').prop('files')[0]
  const parent = feedOpened ? getUser()['id'] : groupData['id']
  $.ajax({
    type : 'POST',
    contentType : 'application/json; charset=utf-8',
    url : 'http://localhost:8080/post/new',
    data : JSON.stringify({
      // post text
      'content' : $('.new-post-input').val(),
      // post author
      'author' : getUser()['id'],
      // time when post is created
      'time' : new Date().getTime(),
      // id of entity where to send post group or user
      'for' : parent,
      // group or user post
      'post_type' : postType
    }),
    success : (result) => {
      // empty post input
      $('.new-post-input').val('')
      if (postPicture != undefined) {
        sendPostPicture(result,postPicture)
      }else if (feedOpened) {
        // update feed posts
        getFeedPosts()
      }else {
        // update group data
        getGroupData(groupData)
        appendPosts(groupData['posts'])
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
  formData.append('post',post['id'])
  $.ajax({
    type : 'POST',
    url : 'http://localhost:8080/post/add/picture',
    contentType : false,
    processData : false,
    data : formData,
    success : (result) => {
      if (feedOpened) {
        // update feed posts
        getFeedPosts()
      }else {
        // update group data
        getGroupData(groupData)
        appendPosts(groupData['posts'])
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
          'src': 'http://localhost:8080/image/' + groupData['profilePhoto']['id'],
          'class': 'group-image'
        }),
      $('<span/>')
        .html(groupData['name'])
    )
}
// follow user or group
function followButton(entity) {
  let followText = 'Follow'
  // user email from cookie
  const email    = getUser()['email']
  // check if user is following entity and change follow button text
  for (i in entity['followers']) {
    // check if user is in entity followers list
    if (entity['followers'][i]['email'] === email) {
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
    .attr('class','followers-container')
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
    .attr('class','follower')
    .append([
      $('<div/>')
        .attr('class','follower-image')
        .append(
          $('<img/>')
            .attr('src','http://localhost:8080/image/' + follower['profilePicture']['id'])
        ),
      $('<span/>')
        .attr('class','follower-name')
        .html(follower['name'])
    ])
}
function getPostAuthor(post_id) {
  let author
  $.ajax({
    type : 'GET',
    url : 'http://localhost:8080/post/author/' + post_id,
    async : false,
    success : (result) => {
      author = result
    }
  })
  return author
}
// add all posts to posts container
function appendPosts(posts) {
  $('.posts').empty()
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
      .html(author['name'])
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
            'src','http://localhost:8080/image/' + author['profilePicture']['id']
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
      .click(() => sendLike(post,'remove/like'))
  }else {
    likeButton
      // change color to grey
      .css('color','grey')
      // on tap will add user like
      .click(() => sendLike(post,'add/like'))
  }
  return likeButton
}
// send group post like with action add/remove
function sendLike(post,action) {
  $.ajax({
    type : 'POST',
    contentType : 'application/json; charset=utf-8',
    url : 'http://localhost:8080/post/' + action,
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
        appendPosts(groupData['posts'])
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
        .attr({
          'class': 'fas fa-comment',
          'id': 'post-' + post['id'] + '-comments-button-icon'
        })
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
        .attr('class','comments-footer')
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
      'overflow': 'auto'
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
    url : 'http://localhost:8080/post/add/comment',
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
// update post comments
function updateComments(post) {
  // remove comments
  $('#comments-container-' + post['id']).remove()
  // update comments container
  $('#post-' + post['id']).append(commentsContainer(post))
  // update comments number
  $('#post-' + post['id'] + '-comments-button-icon').html(' ' + post['comments'].length)
}