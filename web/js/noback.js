/**
 * Created by 王子恒 on 2017/6/11.
 */
//防止页面后退
history.pushState(null, null, document.URL);
window.addEventListener('popstate', function () {
    history.pushState(null, null, document.URL);
});
