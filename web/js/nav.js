var ENV = '';
var NAV = {
    //新闻
    news: {
        url: 'news',
        list: [{
            title: '相关新闻'
        }]
    },
    //科普知识
    knowledge: {
        url: 'knowledge',
        list: [{
            type: 'PROJECT',
            title: '项目知识'
        }, {
            type: 'WELFARE',
            title: '项目知识'
        }]
    },
    //内部活动
    private_activity: {
        url: 'private_activity',
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
            type: 'FINANCIAL,',
            title: '财务部',
        }, {
            type: 'PROJECT,',
            title: '项目部',
        }]
    },
    //培训
    training: {
        url: 'training',
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
            type: 'FINANCIAL,',
            title: '财务部',
        }, {
            type: 'PROJECT,',
            title: '项目部',
        }]
    },
    //公示
    'notice': {
        url: 'notice',
        list: [{
            type: 'PROJECT',
            title: '项目一览'
        }, {
            type: 'HM',
            title: '槐盟公示'
        }]
    },
};

$(function() {
    //  导航栏hover
    // ============================================================
    $(".dropdown").hover(function() {
        $(this).find(".dropdown-toggle").dropdown('toggle');
    }, function() {
        $(this).find(".dropdown-toggle").dropdown('toggle');
    });
})
