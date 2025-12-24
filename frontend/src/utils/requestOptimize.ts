import md5 from 'md5';

//根据请求的地址，方式，参数，统一计算出当前请求的md5值作为key
const getRequestKey = (config) => {
    if (!config) {
        // 如果没有获取到请求的相关配置信息，根据时间戳生成
        return md5(+new Date());
    }

    // 统一处理URL，移除/api前缀，确保请求拦截器和响应拦截器使用相同的URL格式
    let url = (config.url || '').replace('/api', '');

    // 对于GET请求，参数已经在URL中（在request.ts中已处理）
    // 对于POST/PUT等请求，参数在data中
    let paramsStr = '';
    if (config.method === 'get' || config.method === 'GET') {
        // GET请求：参数已经拼接到URL中，直接使用URL即可
        // URL已经包含了所有参数（包括page、pageSize等），所以不同页码的请求URL不同
        paramsStr = url;
    } else {
        // POST/PUT等请求：参数在data中
        const data = typeof config.data === 'string' ? config.data : JSON.stringify(config.data);
        paramsStr = data || '';
    }
    
    // console.log(config,pending,url,md5(url + '&' + config.method + '&' + paramsStr),'config')
    return md5(url + '&' + (config.method || '') + '&' + paramsStr);
}

// 存储key值
const pending = {};
// 检查key值
const checkPending = (key) => !!pending[key];
// 删除key值
const removePending = (key) => {
    // console.log(key,'key')
    delete pending[key];
};

export {
    getRequestKey,
    pending,
    checkPending,
    removePending
}
