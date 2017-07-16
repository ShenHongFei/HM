var ENV = '';
var NAV = {
    activity: {
        url: '/news',
        name: '活动',
        hasUrl: 'true',
        list: [{
            title: '活动预告',
            url: '/activity.html'
        }, {
            title: '活动展示',
            type: 'PRESENTATION',
            url: '/index.html?menu=activity&type=PRESENTATION'
        }, {
            title: '槐盟新闻',
            type: 'ACTIVITY',
            url: '/index.html?menu=activity&type=ACTIVITY'
        }]
    },
    knowledge: {
        url: '/knowledge',
        name: '科普知识',
        hasUrl: 'false',
        list: [{
            type: 'PROJECT',
            title: '项目知识'
        }, {
            type: 'WELFARE',
            title: '公益知识'
        }]
    },
    private_activity: {
        url: '/private-activity',
        name: '内部活动',
        hasUrl: 'false',
        list: [{
            type: 'OFFICE',
            title: '办公室',
        }, {
            type: 'RELATIONS',
            title: '外联部',
        }, {
            type: 'PUBLICITY',
            title: '宣传部',
        }, {
            type: 'FINANCIAL',
            title: '财务部',
        }, {
            type: 'PROJECT',
            title: '项目部',
        }]
    },
    training: {
        url: '/training',
        name: '培训',
        hasUrl: 'false',
        list: [{
            type: 'OFFICE',
            title: '办公室',
        }, {
            type: 'RELATIONS',
            title: '外联部',
        }, {
            type: 'PUBLICITY',
            title: '宣传部',
        }, {
            type: 'FINANCIAL',
            title: '财务部',
        }, {
            type: 'PROJECT',
            title: '项目部',
        }]
    },
    'notice': {
        url: '/notice',
        name: '公示',
        hasUrl: 'false',
        list: [{
            type: 'PROJECT',
            title: '项目一览'
        }, {
            type: 'HM',
            title: '槐盟公示'
        }]
    },
};

// $(function() {
//     //  导航栏hover
//     // ============================================================
//     $(".dropdown").hover(function() {
//         $(this).find(".dropdown-toggle").dropdown('toggle');
//     }, function() {
//         $(this).find(".dropdown-toggle").dropdown('toggle');
//     });
// })
