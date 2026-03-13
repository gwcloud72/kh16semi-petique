// board-detail.js - 게시글 상세(좋아요/댓글 등) UI 스크립트
$(function() {
  var root = document.querySelector('.board-detail[data-board-no]') || document.querySelector('[data-board-no]');
  if (!root) return;

  var base = window.contextPath || '';
  var boardNo = root.getAttribute('data-board-no');
  if (!boardNo) return;

  var loginId = root.getAttribute('data-login-id') || '';
  var replyCategoryNo = root.getAttribute('data-reply-category-no') || '';

  var $likeIcon = $('#board-like');
  var $likeCount = $('#board-like-count');

  if ($likeIcon.length) {
    $.get(base + '/rest/board/check', { boardNo: boardNo }, function(resp) {
      $likeIcon.toggleClass('fa-solid', !!resp.like).toggleClass('fa-regular', !resp.like);
      $likeCount.text(resp.count);
    });

    $likeIcon.on('click', function() {
      if (!loginId) {
        alert('좋아요를 누르려면 로그인하세요.');
        return;
      }

      $.get(base + '/rest/board/action', { boardNo: boardNo }, function(resp) {
        $likeIcon.toggleClass('fa-solid', !!resp.like).toggleClass('fa-regular', !resp.like);
        $likeCount.text(resp.count);
      }).fail(function() {
        alert('좋아요 처리 중 오류가 발생했습니다.');
      });
    });
  }

  var $replyList = $('.reply-list-wrapper');
  if (!$replyList.length) return;

  var $replyCount = $('#reply-count');
  var $replyInput = $('.reply-input');
  var $writeBtn = $('.reply-btn-write');
  var $sortBtns = $('.sort-buttons .btn-sort');
  var $emojiBtn = $('#emoji-btn');
  var $emojiContainer = $('#emoji-picker-container');

  var currentSort = 'time';

  function formatTime(timestamp) {
    if (typeof moment === 'undefined') return timestamp;

    moment.locale('ko');
    var now = moment();
    var time = moment(timestamp);

    if (now.diff(time, 'days') < 1) {
      return time.fromNow();
    }
    if (now.year() === time.year()) {
      return time.format('MM-DD HH:mm');
    }
    return time.format('YYYY-MM-DD HH:mm');
  }

  function parseEmoji(element) {
    if (typeof twemoji === 'undefined' || !element) return;
    setTimeout(function() {
      twemoji.parse(element, { folder: 'svg', ext: '.svg' });
    }, 50);
  }

  function initEmojiPicker() {
    if (!$emojiBtn.length || !$emojiContainer.length || !$replyInput.length) return;

    var emojiList = ['😀','😂','😊','🤣','😅','😆','😍','🥰','😘','😎','🤩','🥳','🤔','😮','😥','😭','🎉','🎁','🎈','🎂','✨','🦄','🐶','❤️'];

    $emojiContainer.empty();
    for (var i = 0; i < emojiList.length; i++) {
      var e = emojiList[i];
      $emojiContainer.append('<span class="emoji-option" data-emoji="' + e + '">' + e + '</span>');
    }

    parseEmoji($emojiContainer[0]);

    var open = false;

    $emojiBtn.on('click', function(ev) {
      ev.stopPropagation();
      $emojiContainer.toggle();
      open = !open;
    });

    $(document).on('click', function(ev) {
      if (open && !$(ev.target).closest('#emoji-picker-container, #emoji-btn').length) {
        $emojiContainer.hide();
        open = false;
      }
    });

    $emojiContainer.on('click', '.emoji-option, img.emoji', function() {
      var emoji = $(this).attr('data-emoji') || $(this).attr('alt');
      if (!emoji) return;

      var input = $replyInput[0];
      if (!input) return;

      var start = input.selectionStart;
      var end = input.selectionEnd;
      var text = input.value;

      input.value = text.substring(0, start) + emoji + text.substring(end);
      input.selectionStart = input.selectionEnd = start + emoji.length;
      input.focus();

      $emojiContainer.hide();
      open = false;
    });
  }

  function escapeHtml(str) {
    return String(str || '')
      .replace(/&/g, '&amp;')
      .replace(/</g, '&lt;')
      .replace(/>/g, '&gt;')
      .replace(/"/g, '&quot;')
      .replace(/'/g, '&#39;');
  }

  function renderReplyItem(reply) {
    var writerBadge = reply.writer ? '<span class="reply-writer-badge">(글쓴이)</span>' : '';
    var heartIconClass = reply.liked ? 'fa-solid' : 'fa-regular';
    var likeSpanClass = reply.liked ? 'active' : '';
    var timeText = formatTime(reply.replyWtime);
	var writerId = reply.replyWriter || '';
	var writerText = escapeHtml(writerId);
	var profileUrl = base + '/member/detail?memberId=' + encodeURIComponent(writerId);

    var actionHtml = '';
    if (reply.owner) {
      actionHtml = '<button class="btn btn-edit" type="button">수정</button> <button class="btn btn-delete" type="button">삭제</button>';
    }

    return (
      '<div class="reply-wrapper" data-reply-no="' + reply.replyNo + '">' +
        '<div>' +
          '<a class="profile-link reply-writer-link" href="' + profileUrl + '"><b>' + writerText + '</b></a>' + writerBadge +
          '<small class="reply-time-text">(' + timeText + ')</small>' +
        '</div>' +
        '<div class="reply-content">' + reply.replyContent + '</div>' +
        '<div class="reply-actions">' +
          '<span class="reply-like ' + likeSpanClass + '" data-reply-no="' + reply.replyNo + '">' +
            '<i class="fa fa-heart ' + heartIconClass + '"></i> <span class="count">' + reply.replyLike + '</span>' +
          '</span>' +
          actionHtml +
        '</div>' +
      '</div>'
    );
  }

  function loadReplies() {
    $replyList.html('<div class="reply-placeholder">댓글을 불러오는 중입니다...</div>');

    $.ajax({
      url: base + '/rest/reply/list',
      method: 'GET',
      data: { replyTarget: boardNo, sort: currentSort, loginId: loginId },
      dataType: 'json',
      success: function(resp) {
        var list = resp.list || [];
        $replyCount.text(resp.boardReply || 0);
        $replyList.empty();

        if (!list.length) {
          $replyList.html('<div class="reply-placeholder">아직 댓글이 없습니다.</div>');
          return;
        }

        for (var i = 0; i < list.length; i++) {
          $replyList.append(renderReplyItem(list[i]));
        }

        parseEmoji($replyList[0]);
      },
      error: function() {
        $replyList.html('<div class="reply-placeholder error">⚠️ 댓글 로드 실패. 서버 오류 또는 네트워크 상태를 확인하세요.</div>');
      }
    });
  }

  $sortBtns.on('click', function() {
    var sortType = $(this).data('sort');
    if (currentSort === sortType) return;

    currentSort = sortType;
    $sortBtns.removeClass('active');
    $(this).addClass('active');
    loadReplies();
  });

  if ($writeBtn.length) {
    $writeBtn.on('click', function() {
      if (!loginId) {
        alert('댓글을 작성하려면 로그인하세요.');
        return;
      }

      var content = ($replyInput.val() || '').trim();
      if (!content) {
        alert('댓글 내용은 비워둘 수 없습니다.');
        return;
      }

      if (!replyCategoryNo) {
        alert('댓글 정보를 불러올 수 없습니다.');
        return;
      }

      var $btn = $(this);
      $btn.prop('disabled', true).text('작성 중...');

      $.post(base + '/rest/reply/write', {
        replyTarget: boardNo,
        replyCategoryNo: replyCategoryNo,
        replyContent: content
      }, function() {
        $replyInput.val('');
        loadReplies();
      }).always(function() {
        $btn.prop('disabled', false).text('댓글 작성');
      });
    });
  }

  $replyList.on('click', '.btn-edit', function() {
    var $wrapper = $(this).closest('.reply-wrapper');
    var $contentEl = $wrapper.find('.reply-content');

    var content = $contentEl.clone().find('img').each(function() {
      $(this).replaceWith($(this).attr('alt') || ' ');
    }).end().text().trim();

    if (!content) content = $contentEl.text().trim();

    $wrapper.find('.edit-mode-container').remove();
    $wrapper.find('.reply-content, .reply-actions').hide();

    $wrapper.append(
      '<div class="edit-mode-container">' +
        '<textarea class="reply-editor" rows="3">' + content + '</textarea>' +
        '<div class="edit-mode-actions">' +
          '<button class="btn btn-positive btn-save" type="button">저장</button>' +
          '<button class="btn btn-cancel" type="button">취소</button>' +
        '</div>' +
      '</div>'
    );
  });

  $replyList.on('click', '.btn-cancel', function() {
    var $wrapper = $(this).closest('.reply-wrapper');
    $wrapper.find('.edit-mode-container').remove();
    $wrapper.find('.reply-content, .reply-actions').show();
  });

  $replyList.on('click', '.btn-save', function() {
    var $wrapper = $(this).closest('.reply-wrapper');
    var replyNo = $wrapper.data('reply-no');
    var newContent = ($wrapper.find('.reply-editor').val() || '').trim();

    if (!newContent) {
      alert('댓글 내용은 비워둘 수 없습니다.');
      return;
    }

    var $btn = $(this);
    $btn.prop('disabled', true).text('저장 중...');

    $.post(base + '/rest/reply/edit', { replyNo: replyNo, replyContent: newContent }, function() {
      loadReplies();
    }).always(function() {
      $btn.prop('disabled', false).text('저장');
    });
  });

  $replyList.on('click', '.btn-delete', function() {
    if (!confirm('정말 삭제하시겠습니까?')) return;

    var $wrapper = $(this).closest('.reply-wrapper');
    var replyNo = $wrapper.data('reply-no');

    var $btn = $(this);
    $btn.prop('disabled', true).text('삭제 중...');

    $.post(base + '/rest/reply/delete', { replyNo: replyNo }, function() {
      loadReplies();
    }).always(function() {
      $btn.prop('disabled', false).text('삭제');
    });
  });

  $replyList.on('click', '.reply-like', function() {
    if (!loginId) {
      alert('좋아요는 로그인 후 이용 가능합니다.');
      return;
    }

    var replyNo = $(this).data('reply-no');
    var $likeSpan = $(this);
    var $heartIcon = $likeSpan.find('i');
    var $count = $likeSpan.find('.count');

    $.post(base + '/rest/reply/like/action', { replyNo: replyNo }, function(resp) {
      $count.text(resp.count);
      $likeSpan.toggleClass('active', resp.liked);
      $heartIcon.toggleClass('fa-solid', resp.liked).toggleClass('fa-regular', !resp.liked);
    });
  });

  initEmojiPicker();
  loadReplies();
});
