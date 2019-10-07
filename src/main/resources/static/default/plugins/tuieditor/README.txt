-------------------------------------------------------------------
Mady By Vorfeed
문의사항 Sir.kr
-------------------------------------------------------------------
해당 tuieditor 폴더를 plugin/editor에 업로드 해주세요.

CSS폴더에 markdown.css업로드
js 폴더의 juqery.3.0.0.js, jquery-migrate-3.0.1.js를 업로드합니다

head.sub.php에서

<link rel="stylesheet" href="<?php echo G5_CSS_URL?>/markdown.css">
<script src="<?php echo G5_JS_URL ?>/jquery-3.0.0.js"></script>
<script src="<?php echo G5_JS_URL ?>/jquery-migrate-3.0.1.js"></script>

해주시고 기존의 jquery-.1.8.3.min.js는 주석처리하거나 제거해주세요.