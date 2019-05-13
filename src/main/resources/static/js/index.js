feedOpened   = true;
groupOpened  = false;
userOpened   = false;
searchOpened = false;

function onLoad() {
  onloadRedirect();
  newsFeed();
  getUserData(getUser(), (account) => accountButton(account));
}
function accountButton(account) {
  $('.account')
    .append(
      $('<div/>')
        .attr('class','account-image')
        .append(
          $('<img/>')
            .attr('src',`http://localhost:8080/images/${account['profilePicture']['id']}`)
        ),
      $('<p/>')
        .html(account['name'])
    )
    .click(() => renderUserContainer(account));
}
// return cookie data
function getUser() {
  return JSON.parse(Cookies.get('account-data'));
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
      .html('Image not selected');
  }else {
    $('#field-error')
      .css('display','block')
      .html('Field cannot be empty');
  }
}
// hide error field if something is typed in group name field
document.getElementById('group-name').onkeypress = () => {
  $('#field-error').html('');
}
// add search field to navigation
function showSearchField() {
  searchUsers  = true;
  searchGroups = false;
  $('.nav-wrapper')
    .append(
      $('<input/>')
        .attr({
          'class': 'search search-field',
          'placeholder': 'Search users or groups'
        })
        .on('keyup', () => {
          if ($('.search-field').val() !== '') {
            // search user or group in database when user is typing
            if (searchUsers) {
              searchEntity('user')
            }else if (searchGroups) {}{
              searchEntity('group')
            }
          }
        }),
      // result div
      searchResult()
    );
  changeSearchColors();
  searchOpened = true;
}
function closeSearchField() {
  $('.search').remove();
  searchOpened = false;
}
function changeSearchColors() {
  if (searchGroups) {
    $('.search-group')
      .css('color','#f44242');
    $('.search-user')
      .css('color','white');
  }else {
    $('.search-user')
      .css('color','#f44242');
    $('.search-group')
      .css('color','white');
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
          searchGroups = false;
          searchUsers  = true;
          changeSearchColors();
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
          searchGroups = true;
          searchUsers  = false
          changeSearchColors();
        })
    );
}
// search user or group in database
function searchEntity(entity) {
  var findedEntity;
  $.ajax({
    type  : 'GET',
    url   : `http://localhost:8080/${entity}/name=${$('.search-field').val()}`,
    success : (result) => {
      // check if entity finded and div with entity is not already created
      if (result !== '' && $(`#search-entity-${result['id']}`).length === 0) {
        // append to search result finded user or group
        $('.search-result').append(searchResultEntity(result,entity));
        // save finded entity
        findedEntity = $.extend(true, {} ,result);
      // check if any entity was founded
      }else if (result === '' && findedEntity !== undefined) {
        // remove previously finded entity div
        $('#search-entity-' + findedEntity['id']).remove();
      }
    }
  });
}
// div for finded user or group
function searchResultEntity(entity,type) {
  return $('<div>')
    .attr({
      'class': 'search-entity',
      'id': 'search-entity-' + entity['id']
    })
    .append(
      $('<div/>')
        .attr('class','search-entity-image')
        .append(
          $('<img/>')
            .attr('src',`http://localhost:8080/images/${entity['profilePicture']['id']}`)
        ),
      $('<span/>')
        .attr('class','search-entity-name')
        .html(entity['name'])
    )
    // get user or group data
    .click(() => $.getJSON(`http://localhost:8080/${entity['type'].toLowerCase()}/name=${entity['name']}`, (result) => {
      $('.search').remove();
      // render user or group container
      if (entity['type'] === 'USER') {
        renderUserContainer(result);
      }else {
        groupData = $.extend(true, {}, result);
        renderGroupContainer(result);
      }
    }));
}
// check login status
async function onloadRedirect() {
  // check if cookie exist
  if (Cookies.get('account-data') === undefined) {
    window.location.href = "/login";
  }else {
    getUserGroups();
    // render persons who user follows at right panel
    $.getJSON(`http://localhost:8080/user/following/${getUser()['id']}`, (result) => {
      $('.following-box').empty();
      sortJsonObjects(result).forEach(follower => renderFollowing(follower));
    });
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
    );
  getFeedPosts();
}
// get home feed posts
function getFeedPosts() {
  $.getJSON(`http://localhost:8080/user/feed/${getUser()['id']}`, (result) => {
    if (result.length == 0) {
      $('.posts').append(
        $('<span/>')
          .attr('class','empty-feed-text')
          .html('Wow,such empty,follow some groups or users first'),
        $('<div/>')
          .attr('class','empty-box')
          .append($('<img/>').attr('src','../icons/empty.svg'))
      );
    }else {
      appendPosts(result);
    }
  })
}
/* json from ajax give objects in random
  so we sort them by id */
function sortJsonObjects(objects) {
  for (let i = 0; i < objects.length; i++) {
    for(let j = 0; j < objects.length; j++) {
      if (objects[i]['id'] > objects[j]['id']) {
        const temp = objects[i];
        objects[i] = objects[j];
        objects[j] = temp;
      }
    }
  }
  return objects;
}
// create new group request
function createGroup() {
  const formData = new FormData();
  // image file
  formData.append('image', $("#choose-group-image").prop('files')[0]);
  // background file
  formData.append('background', $("#choose-group-background").prop('files')[0]);
  // group name
  formData.append('name' , $('#group-name').val());
  // group admin email
  formData.append('admin', getUser()['email']);
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
        $('.groups-box').empty();
        // update user groups
        getUserGroups();
      }else {
        // empty name field
        $('#group-name').val('');
        // show field error
        $('#field-error')
          .show()
          .html('group already exist');
      }
    }
  });
}
// get group data like posts,images,followers...
function getGroupData(group, callback) {
  $.getJSON(`http://localhost:8080/group/id=${group['id']}`, (result) => {
    groupData = $.extend(true, {}, result);

    if (callback !== undefined) {
      callback(result)
    }
  });
}
function getGroupPosts(group, callback) {
  $.getJSON(`http://localhost:8080/group/posts/${group['id']}`, (result) => {
    callback(result);
  });
}
function getUserData(user, callback) {
  $.getJSON(`http://localhost:8080/user/name=${user['name']}`, (result) => callback(result));
}
// request user groups json
function getUserGroups() {
  $.getJSON(`http://localhost:8080/user/groups/${getUser()['email']}`, (result) => {
      $('.groups-box').empty();
      // render all groups in left panel
      sortJsonObjects(result).forEach(group => renderGroup(group))
  });
}
function renderUserContainer(user) {
  // remove news feed
  $('.feed').remove();
  feedOpened  = false;
  groupOpened = false;
  userOpened  = true;
  // hide right panel containing friends
  $('.right-panel').css('display','none');
  // get user followers list,request is async so we execute all other work inside
  $.getJSON(`http://localhost:8080/user/followers/${user['id']}`, (followers) => {
    // add user container
    $('.main-container').append(
      $('<div/>')
        .attr('class','entity-container')
        // append user container elements
        .append(
          // header
          $('<div/>')
            .attr('class','entity-header')
            .css(
              'background-image',
              `url(http://localhost:8080/images/${user['backgroundPicture']['id']})`
            )
            .append(
              // overlay making user background image darker
              $('<div/>')
                .attr('class','entity-header-overlay'),
              // close button
              containerClose(),
              $('<p/>')
                .html(`${followers.length}  Follower(s)`),
              // container storing profile image
              entityContainerImage(user),
            ),
          $('<div/>')
            .attr('class','content')
            .append(
              $('<div/>').attr('class','posts')
            )
        ),
      followersContainer(followers)
    );
    // add follow button if user for container isn't client
    if (user['email'] !== getUser()['email']) {
      $('.entity-header p').css({'bottom' : '0px','left' : '10px'});
      $('.entity-header').append(followButton(user, followers).css({
        'margin-top' : '185px',
        'margin-left': '10px'
      }));
    }else {
      $('.entity-header').append(newPostContainer());
    }
    appendPosts(user['posts']);
  });
}
async function renderFollowing(user) {
  // append user div to following list
  $('.following-box').append(userDiv(user).attr('class','following-user'));
}
// add following groups list at left panel
async function renderGroup(group) {
  // append group div to group list
  $('.groups-box').append(
    $('<button/>')
      .html(group['name'])
      .attr('class','group')
      .click(() => {
        $('.entity-container').remove();
        getGroupData(group, (result) => renderGroupContainer(result));
      }
    )
  )
}
async function renderGroupContainer(group) {
  // remove news feed
  $('.feed').remove();
  feedOpened  = false;
  groupOpened = true;
  userOpened  = false;
  // hide right panel containing friends
  $('.right-panel').css('display','none');
  //
  $.getJSON(`http://localhost:8080/group/followers/${group['id']}`,(followers) => {
    // add group container
    $('.main-container').append(
      $('<div/>')
        .attr('class','entity-container')
        // append group container elements
        .append(
          // header
          $('<div/>')
            .attr('class','entity-header')
            .css(
              'background-image',
              `url(http://localhost:8080/images/${group['backgroundPicture']['id']})`
            )
            .append(
              // overlay making group background image darker
              $('<div/>')
                .attr('class','entity-header-overlay'),
              // close button
              containerClose(),
              followButton(group, followers),
              // container with new post input/submit
              newPostContainer("Group"),
              $('<p/>')
                .css('margin-left','85px')
                .html(`${followers.length}  Follower(s)`),
              // container storing group images
              entityContainerImage(group),
            ),
          $('<div/>')
            .attr('class','content')
            .append(
              $('<div/>').attr('class','posts')
            )
        ),
      followersContainer(followers)
    );
    getGroupPosts(group, (posts) => appendPosts(posts));
  });
}
// close button for container
function containerClose() {
  return $('<span/>')
    .attr('id','close')
    .append('<i/>')
      .attr('class','fas fa-chevron-left')
      .click(() => {
          // show right panel containing friends
          $('.right-panel').show();
          // remove followers conta iner
          $('.followers-container').remove();
          // remove group container
          $('.entity-container').remove();
          feedOpened  = true;
          groupOpened = false;
          userOpened  = false;
          // restore news feed
          newsFeed();
          // update group list
          getUserGroups();
          $.getJSON(`http://localhost:8080/user/following/${getUser()['id']}`, (result) => {
            $('.following-box').empty();
            sortJsonObjects(result).forEach(follower => renderFollowing(follower));
          });
      });
}
// new post container
function newPostContainer(postType) {
  return $('<div/>')
    .attr('class','new-post')
    // add elements to new post container
    .append(
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
    );
}
// send new post to group or user wall
function sendNewPost(postType) {
  const postPicture = $('.choose-post-picture').prop('files')[0];
  const parent = feedOpened || userOpened ? getUser()['id'] : groupData['id'];
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
      $('.new-post-input').val('');

      if (postPicture !== undefined) {
        sendPostPicture(result,postPicture);
      }else if (feedOpened) {
        // update feed posts
        getFeedPosts();
      }else if (userOpened) {
        getUserData(getUser(), (user) => appendPosts(user['posts']))
      }else {
        getGroupPosts(groupData, (posts) => appendPosts(posts));
      }
    }
  });
}
// add picture to post after post was send
function sendPostPicture(post,postPicture) {
  const formData = new FormData();
  // picture file
  formData.append('post_picture', postPicture);
  // post data
  formData.append('post',post['id']);
  $.ajax({
    type : 'POST',
    url : 'http://localhost:8080/post/add/picture',
    contentType : false,
    processData : false,
    data : formData,
    success : (result) => {
      if (feedOpened) {
        // update feed posts
        getFeedPosts();
      }else {
        getGroupPosts(groupData, (posts) => appendPosts(posts));
      }
    }
  });
}
function entityContainerImage(entity) {
  // top right container that stores group image
  const imageContainer = $('<div/>')
    .attr('class','entity-image-container')
    // container elements
    .append(
      // group image
      $('<img/>')
        .attr({
          'src': `http://localhost:8080/images/${entity['profilePicture']['id']}`,
          'class': 'entity-image'
        })
    );
    // show only name or name and surname
    if (entity['type'] === 'GROUP') {
      imageContainer.append(
        $('<span/>').html(`${entity['name']}`)
      );
    }else {
      imageContainer.append(
        $('<span/>').html(`${entity['name']} ${entity['surname']}`)
      );
    }
    return imageContainer;
}
// follow user or group
function followButton(entity, followers) {
  let followText = 'Follow';
  // check if user is following entity and change follow button text
  followers.forEach(follower => {
    // check if user is in entity followers list
    if (follower['email'] === getUser()['email']) {
      // change follow button text
      followText = 'Following';
    }
  });
  // group follow button
  return $('<button/>')
    .attr('class','follow-button')
    .html(followText)
    .click(() => {
      if ($('.follow-button').html() === 'Following') {
        sendUnfollow(entity);
      }else {
        sendFollow(entity);
      }
    })
}
// send request to follow to user or group
function sendFollow(entity) {
  $.ajax({
    type : 'POST',
    url  : `http://localhost:8080/${entity['type'].toLowerCase()}/follow/${entity['id']}`,
    data : {'email' : getUser()['email']},
    // change follow button text
    success : () => {
      $('.follow-button').html('Following')
      // add user to followers container
      updateFollowersContainer(entity);
    }
  });
}
function sendUnfollow(entity) {
  $.ajax({
    type : 'POST',
    url  : `http://localhost:8080/${entity['type'].toLowerCase()}/unFollow/${entity['id']}`,
    data : {'email' : getUser()['email']},
    // change follow button text
    success : () => {
      $('.follow-button').html('Follow');
      // remove user from followers container
      updateFollowersContainer(entity);
    }
  });
}
// update user or group followers container
function updateFollowersContainer(entity) {
  $.getJSON(`http://localhost:8080/${entity['type'].toLowerCase()}/followers/${entity['id']}`,
    (updatedFollowers) => {
      $('.followers').empty();
      // add all followers to right side
      sortJsonObjects(updatedFollowers).forEach(follower => {
        $('.followers-container')
          .find('.followers')
          .append(userDiv(follower));
      });
    }
  );
}
// container with followers at right side
function followersContainer(followers) {
  const followersContainer = $('<div/>')
    .attr('class','followers-container')
    .append(
      $('<div/>')
        .attr('class','followers')
    );
  // add all followers to right side
  followers.forEach(follower => {
    followersContainer
      .find('.followers')
      .append(userDiv(follower));
  });
  return followersContainer;
}
function userDiv(follower) {
  return $('<div>')
    .attr('class','user')
    .append(
      $('<div/>')
        .attr('class','follower-image')
        .append(
          $('<img/>')
            .attr('src',`http://localhost:8080/images/${follower['profilePicture']['id']}`)
        ),
      $('<span/>')
        .html(`${follower['name']} ${follower['surname']}`)
    )
    .click(() => renderUserContainer(follower));
}
function getPostAuthor(post) {
  let author;
  $.ajax({
    type : 'GET',
    url : `http://localhost:8080/post/author/${post['id']}`,
    async : false,
    success : (result) => {
      author = result;
    }
  });
  return author;
}
// add all posts to posts container
function appendPosts(posts) {
  $('.posts').empty();
  sortJsonObjects(posts).forEach(post => {
    // add post to feed
    $('.posts').append(postDiv(post));
    // if post contains picture add img element
    if (post['picture'] !== null) {
      $('.post-' + post['id'] + '-content-box')
        .append(
          // picture
          $('<img/>')
            .attr({
              'class': 'post-picture',
              'src': `http://localhost:8080/post/picture/${post['id']}`
            })
        );
    }
  });
}
function postDiv(post) {
  const postDiv = $('<div/>')
    .attr({
      'class': 'post',
      'id': `post-${post['id']}`
    })
    .append(
      // post header
      $('<div/>')
        .attr('class','post-header')
        .append(
          // post author image and name
          postAuthor(post),
          postGroup(post),
          // time when post was created
          $('<span/>')
            .attr('class','post-time')
            .html(timeConverter(post['time']))
        ),
      // content text
      $('<div/>')
        .attr('class',`post-${post['id']}-content-box`)
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
    );
    // add comments container if post contains any picture
    if (post['picture'] !== null) {
      postDiv.append(commentsContainer(post));
    }
    return postDiv;
}
// convert time from long to hour and minute format
function timeConverter(time) {
    return new Date(parseInt(time))
      .toLocaleTimeString()
      .replace(/:\d+ /, ' ');
}
function postGroup(post) {
  let groupDiv;
  if (post['group'] !== null) {
    groupDiv = $('<div/>')
      .attr('class','post-group')
      .append(
        // separator
        $('<i/>')
          .attr('class','fas fa-chevron-right')
          .css({
            'color': '#F44242',
            'float': 'left',
            'line-height': '30px',
            'padding-right': '15px'
          }),
        $('<div/>')
          .attr('class','post-author-image')
          .append(
            $('<img/>')
              .attr(
                'src',`http://localhost:8080/images/${post['group']['profilePicture']['id']}`
              )
          ),
        $('<span/>')
          .html(post['group']['name'])
          .css({
            'line-height':'200%',
            'margin-left':'10px'
          })
      )
      .click(() => renderGroupContainer(post['group']));  
  }
  return groupDiv;
  
}
// post author image and username
function postAuthor(post) {
  author = getPostAuthor(post);
  return [
    // username
    $('<span/>')
      .html(`${author['name']} ${author['surname']}`)
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
            'src',`http://localhost:8080/images/${author['profilePicture']['id']}`
          )
      )
  ];
}
// like button with heart icon
function likeButton(post) {
  // user liked this post true/false
  let liked;
  // check if user already liked post
  if (post['likes'].includes(getUser()['id'])) {
    liked = true;
  }else {
    liked = false;
  }
  const likeButton = $('<button/>')
    .attr('class','like-button')
    .append(
      $('<i/>')
        .attr('class','fas fa-heart')
        // likes count number on icon
        .html(` ${post['likes'].length}`)
    );
  // like button settings based on liked status
  if (liked) {
    likeButton
      // change button color to red
      .css('color','#F44256')
      // on tap will remove user like
      .click(() => sendLike(post,'remove/like'));
  }else {
    likeButton
      // change color to grey
      .css('color','grey')
      // on tap will add user like
      .click(() => sendLike(post,'add/like'));
  }
  return likeButton;
}
// send group post like with action add/remove
function sendLike(post,action) {
  $.ajax({
    type : 'POST',
    contentType : 'application/json; charset=utf-8',
    url : `http://localhost:8080/post/${action}`,
    data : JSON.stringify({
      'post_id'   : post['id'],
      'author_id' : getUser()['id']
    }),
    success: () => {
      // update posts list
      if (feedOpened) {
        getFeedPosts();
      }else if (groupOpened){
        getGroupPosts(groupData, (posts) => appendPosts(posts));
      }
    }
  });
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
          'id': `post-${post['id']}-comments-button-icon`
        })
        // comments count number on icon
        .html(` ${post['comments'].length}`)
    );
}
// container containing comments
function commentsContainer(post) {
  return $('<div/>')
    .attr({
      'class': 'comments-container',
      'id': `comments-container-${post['id']}`
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
            .click(() => sendComment(post))
        )
    );
}
// comments divs
function postComments(post) {
  const commentsBox = $('<div/>')
    .attr('class','comments')
    .css({
      'overflow': 'auto'
    });
  // add all comments divs
  sortJsonObjects(post['comments']).forEach(comment => {
    commentsBox.append(
      // comment div
      $('<div/>')
        .attr('class','comment')
        // comment elements
        .append(
          // author
          postAuthor(comment),
          // content
          $('<p/>')
            .css({
              'margin-top':'5px',
              'overflow-wrap':'break-word'
            })
            .html(comment['content']),
        )
    );
  });
  return commentsBox;
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
    success : (result) => {
      // update post comments
      updateComments(result);
    }
  });
}
// update post comments
function updateComments(post) {
  // remove comments
  $(`#comments-container-${post['id']}`).remove();
  // update comments container
  $(`#post-${post['id']}`).append(commentsContainer(post));
  // update comments number
  $(`#post-${post['id']}-comments-button-icon`).html(` ${post['comments'].length}`);
}
