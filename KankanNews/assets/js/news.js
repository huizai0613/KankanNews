window.onload = function () {
	initialize();
}
function initialize() {
	initTitle();
	initNewsDate();
	initNewsIntro();
	setWebWidth();
	initNewsContent();
	initNewsRelated();
}
function setWebWidth() {
	if (window.news) {
		news.setWebWidth(document.body.clientWidth);
	}
}
function initTitle() {
	dTitle = document.getElementById("title");
	if (window.news) {
		var title = news.getTitle();
		dTitle.innerHTML = title;
	}
}
function initNewsDate() {
	dNewsDate = document.getElementById("news_date");
	if (window.news) {
		var newsDate = news.getDate();
		dNewsDate.innerHTML = newsDate;
	}
}
function initNewsIntro() {
	dNewsIntro = document.getElementById("news_intro");
	if (window.news) {
		var intro = news.getIntro();
		dNewsIntro.innerHTML = intro;
	}
}
function initNewsContent() {
	dNewsContent = document.getElementById("news_content");
	if (window.news) {
		var content = news.getContent();
		dNewsContent.innerHTML = content;
	}
}
function initNewsRelated() {
	dNewsRecommend = document.getElementById("news_recommend");
	if (window.news) {
		var recommend = news.getRecommend();
		dNewsRecommend.innerHTML = recommend;
	}
}
function showImage(image, id) {
	imageView = document.getElementById(id);
	imageView.src = image;
}
function changeImageProcess(process, id) {
	imageView = document.getElementById(id);
	imageView.src = 'images/loading_' + process + '.png';
}
function openNews(id, type) {
	news.openNews(id, type);
}
function openVideo(id) {
	var video_1 = document.getElementById(id);
	news.openVideo(id, video_1.offsetLeft, video_1.offsetTop);
}
function showBody() {
	var bodyHtml = document.body.innerHTML;
	//alert(document.body.offsetWidth);
	//window.news.showBody(window.height + '   ' + window.width);
	window.news.showBody(bodyHtml);
}
