window.onload = function () {
	initialize();
}
function initialize() {
	initFontSize();
	initTitle();
	initNewsDate();
	initNewsAuthor();
	initNewsIntro();
	setWebWidth();
	initNewsContent();
	initNewsRelated();
	//testAlign();
}
function setWebWidth() {
	if (window.news) {
		news.setWebWidth(document.body.clientWidth);
	}
}
function initFontSize() {
	if (window.news) {
		var fontSize = news.initFontSize();
		changeFontSize(fontSize);
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
function initNewsAuthor() {
	dNewsAuthor = document.getElementById("news_author");
	if (window.news) {
		var author = news.getAuthor();
		dNewsAuthor.innerHTML = author;
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
function openNews(id, type, title) {
	news.openNews(id, type, title);
}
function previewImage(id) {
	news.previewImage(id);
}
function openVideo(id) {
	var video_1 = document.getElementById(id);
	news.openVideo(id, video_1.offsetLeft, video_1.offsetTop);
}
function changeFontSize(font) {
	document.body.className = font;
}
function showBody() {
	var bodyHtml = document.getElementsByTagName('html')[0].innerHTML;
	//alert(document.body.offsetWidth);
	//window.news.showBody(window.height + '   ' + window.width);
	window.news.showBody(bodyHtml);
}
function testAlign() {
	var allp=document.getElementsByTagName('p');//选择所有p标签
	var i=0;
	for(i=0;i<allp.length;i++){//对于每一个p标签
		var allchar=allp[i].innerHTML.split('');//将p标签内容的所有字符打散
		var j=0;
		var istag=false;//标识是否属于标签内部'<***>'
		var isch=true;//标识是否是中文，作用是判断下一个字符前是否需要加空格
		for(j=0;j<allchar.length-1;j++){
			if(allchar[j]=='<'){//标识标签的起始位置
				istag=true;
			}else if(allchar[j]=='>'){//标识标签的结束
				istag=false;
			}else if(istag==false){//对于标签'<>'以外的字符
				if(/[\u4e00-\u9fa5]/.test(allchar[j])){//如果是中文
					if(isch==true){//如果前一个字符也是中文（或空格，见下），它前面就已经有空格了，不再添加
						if(allchar[j+1]!='	'){//如果后一个字符是空格，那么它后面也不用加空格了，因为按照规则，紧邻的空格，后面的全都无效
							allchar[j]=allchar[j]+'	';
						}
					}else{//如果前一个字符不是中文（或不是空格），则它前面应该没有空格，加一个
						if(allchar[j+1]=='	'){//同上，判断后面是不是空格
							allchar[j]='	'+allchar[j];
						}else{
							allchar[j]='	'+allchar[j]+' ';
						}
					}
					isch=true;//更新中文标识
				}else if(allchar[j]=='	'){
					allchar[j]='<span style="letter-spacing:.5em;"> </span>';//对于本来就存在的空格，为避免word-spacing的负值导致空格不明显，这里单独调大空格的占位宽度
					isch=true;//既然这里有了空格，那么后面就不需要再有了，所以将isch标志改为true
				}else{
					isch=false;//更新中文标识
				}
			}
		}
		allp[i].innerHTML=allchar.join('');//将打散的字符再次拼接，作为p标签的内容
		allp[i].style.wordSpacing='-.15em';//设置p标签内的单词间距为负，变相隐藏中文间添加的空格，注意，不是字母间距，因为字母间没有空格
	}
}
