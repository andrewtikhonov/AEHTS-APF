function initPost(context)
{
	$('span.post-hr', context).html('<hr align="left" />');
	initQuotes(context);
	initExternalLinks(context);
	initPostImages(context);
	initSpoilers(context);
	initMedia(context);
}
function initQuotes(context)
{
	$('div.q', context).each(function(){
		var $q = $(this);
		var name = $(this).attr('head');
		var q_title = (name ? '<b>'+name+'</b> писал(а):' : '<b>Цитата:</b>');
		if ( quoted_pid = $q.children('u.q-post:first').text() ) {
			var on_this_page = $('#post_'+quoted_pid).length;
			var href = (on_this_page) ? '#'+ quoted_pid : './viewtopic.php?p='+ quoted_pid +'#'+ quoted_pid;
			q_title += ' <a href="'+ href +'" title="Перейти к цитируемому сообщению"><img src="http://static.rutracker.org/templates/default/images/icon_latest_reply.gif" class="icon2" alt="" /></a>';
		}
		$q.before('<div class="q-head">'+ q_title +'</div>');
	});
}
function initPostImages(context)
{
	if (hidePostImg) return;
	var $in_spoilers = $('div.sp-body var.postImg', context);
	$('var.postImg', context).not($in_spoilers).each(function(){
		var $v = $(this);
		var src = $v.attr('title');
		var $img = $('<img src="'+ src +'" class="'+ $v.attr('class') +'" alt="pic" />');
		$img = fixPostImage($img);
		var maxW = ($v.hasClass('postImgAligned')) ? postImgAligned_MaxWidth : postImg_MaxWidth;
		$img.bind('click', function(){ return imgFit(this, maxW); });
		if (user.opt_js.i_aft_l) {
			$('#preload').append($img);
			var loading_icon = '<a href="'+ src +'" target="_blank"><img src="http://static.rutracker.org/templates/default/images/loading_3.gif" alt="" /></a>';
			$v.html(loading_icon);
			if ($.browser.msie) {
				$v.after('<wbr>');
			}
			$img.one('load', function(){
				imgFit(this, maxW);
				$v.empty().append(this);
			});
		}
		else {
			$img.one('load', function(){ imgFit(this, maxW) });
			$v.empty().append($img);
			if ($.browser.msie) {
				$v.after('<wbr>');
			}
		}
	});
}
function initSpoilers(context)
{
	$('div.sp-body', context).each(function(){
		var $sp_body = $(this);
		var name = $.trim(this.title) || 'скрытый текст';
		this.title = '';
		var $sp_head = $('<div class="sp-head folded clickable">'+ name +'</div>');
		$sp_head.insertBefore($sp_body).click(function(e){
			if (!$sp_body.hasClass('inited')) {
				initPostImages($sp_body);
				var $sp_fold_btn = $('<div class="sp-fold clickable">[свернуть]</div>').click(function(){
					$.scrollTo($sp_head, { duration:200, axis:'y', offset:-200 });
					$sp_head.click().animate({opacity: 0.1}, 500).animate({opacity: 1}, 700);
				});
				$sp_body.prepend('<div class="clear"></div>').append('<div class="clear"></div>').append($sp_fold_btn).addClass('inited');
			}
			if (e.shiftKey) {
				e.stopPropagation();
				e.shiftKey = false;
				var fold = $(this).hasClass('unfolded');
				$('div.sp-head', $($sp_body.parents('td')[0])).filter( function(){ return $(this).hasClass('unfolded') ? fold : !fold } ).click();
			}
			else {
				$(this).toggleClass('unfolded');
				$sp_body.slideToggle('fast');
			}
		});
	});
}
function initExternalLinks(context)
{
	$("a.postLink:not([href*='"+ window.location.hostname +"/'])", context).attr({ target: '_blank' });
}
function fixPostImage ($img)
{
	var banned_image_hosts = /tinypic|imagebanana|hidebehind|ipicture|centrkino/i;  // imageshack
	var src = $img[0].src;
	if (src.match(banned_image_hosts)) {
		$img.wrap('<a href="'+ this.src +'" target="_blank"></a>').attr({ src: "http://static.rutracker.org/smiles/tr_oops.gif", title: "Прочтите правила выкладывания скриншотов!" });
	}
	return $img;
}
function initMedia(context)
{
	var apostLink = $('a.postLink', context);
	for (var i = 0; i < apostLink.length; i++) {
		var link = apostLink[i];
		if (typeof link.href != 'string') {
			continue;
		}
		if (/^http(?:s|):\/\/www.youtube.com\/watch\?(.*)?(&?v=([a-z0-9\-_]+))(.*)?|http:\/\/youtu.be\/.+/i.test(link.href)) {
			var a = document.createElement('span');
			a.className = 'YTLink';
			a.innerHTML = '<span title="Начать проигрывание на текущей странице" class="YTLinkButton">&#9658;</span>';
			window.addEvent(a, 'click', function (e) {
				var vhref = e.target.nextSibling.href.replace(/^http(?:s|):\/\/www.youtube.com\/watch\?(.*)?(&?v=([a-z0-9\-_]+))(.*)?|http:\/\/youtu.be\//ig, "http://www.youtube.com/embed/$3");
				var text  = e.target.nextSibling.innerText != "" ? e.target.nextSibling.innerText : e.target.nextSibling.href;
				$('#Panel_youtube').remove();
				ypanel('youtube', {
					title: '<b>' + text + '</b>',
					resizing: 0,
					width: 862,
					height: 550,
					content: '<iframe width="853" height="510" frameborder="0" allowfullscreen="" src="' + vhref + '?wmode=opaque"></iframe>'
				});
			});
			link.parentNode.insertBefore(a, link);
			a.appendChild(link);
		}
	}
	$('a[href^="http://soundcloud.com/"]', context).each(function(){
		if ($.browser.msie && $.browser.version < 9) {
			return;
		}
		var $a = $(this);
		var sc_url = $a.attr('href');
		var $m_span = build_m_link($a);

		$m_span.click(function(e){
			e.preventDefault();
			if (typeof SC == 'undefined') {
				$.ajax({
					url: "http://connect.soundcloud.com/sdk.js", dataType: "script", cache: true, global: false,
					success: function(){ sc_embed($m_span, sc_url) }
				});
			}
			else {
				sc_embed($m_span, sc_url);
			}
		});
	});
}
function build_m_link ($a)
{
	return $a.wrap('<span class="m-link" title="Начать проигрывание"></span>').after('<span class="m-icon">&#9658;</span>').parent();
}
function sc_embed ($m_span, sc_url)
{
	var $player_div = $('<div style="clear: both; margin: 8px 0 2px;"><i class="loading-1"></i></div>');
	$m_span.after($player_div).remove();
	SC.oEmbed(sc_url, {auto_play: false}, $player_div[0]);
}

function getElText (e)
{
	var t = '';
	if (e.textContent !== undefined) {
		t = e.textContent;
	}
	else if (e.innerText !== undefined) {
		t = e.innerText;
	}
	else {
		t = jQuery(e).text();
	}
	return t;
}
function escHTML (txt) {
	return txt.replace(/</g, '&lt;');
}
function cfm (txt)
{
	return window.confirm(txt);
}
function post2url (url, params) {
	params = params || {};
	var f = document.createElement('form');
	f.setAttribute('method', 'post');
	f.setAttribute('action', url);
	f.setAttribute('target', params['target'] || '_self');
	params['form_token'] = '';
	for (var k in params) {
		var h = document.createElement('input');
		h.setAttribute('type', 'hidden');
		h.setAttribute('name', k);
		h.setAttribute('value', params[k]);
		f.appendChild(h);
	}
	document.body.appendChild(f);
	f.submit();
	return false;
}
