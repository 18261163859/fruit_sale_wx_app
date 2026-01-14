/**
 * 图片工具函数
 */

// 默认占位图URL（使用公开的占位图服务）
const PLACEHOLDER_IMAGES = {
  banner: 'https://images.unsplash.com/photo-1619566636858-adf3ef46400b?w=750&h=400&fit=crop', // 水果横幅
  product: 'https://images.unsplash.com/photo-1610832958506-aa56368176cf?w=400&h=400&fit=crop', // 水果商品
  avatar: 'https://images.unsplash.com/photo-1472099645785-5658abf4ff4e?w=200&h=200&fit=crop', // 头像
  category: 'https://images.unsplash.com/photo-1619566636858-adf3ef46400b?w=300&h=300&fit=crop' // 分类
};

// 水果图片映射（使用unsplash的真实水果图片）
const FRUIT_IMAGES = {
  // Banner图片
  'mango.jpg': 'https://images.unsplash.com/photo-1605664515998-c9371a2aa9cd?w=750&h=400&fit=crop',
  'pitaya.jpg': 'https://images.unsplash.com/photo-1603833797131-3c0a6e750d7e?w=750&h=400&fit=crop',
  'intro.jpg': 'https://images.unsplash.com/photo-1619566636858-adf3ef46400b?w=750&h=400&fit=crop',
  'vip.jpg': 'https://images.unsplash.com/photo-1464454709131-ffd692591ee5?w=750&h=400&fit=crop',

  // 商品图片 - 芒果
  'mango1.jpg': 'https://images.unsplash.com/photo-1605664515998-c9371a2aa9cd?w=500&h=500&fit=crop',
  'mango2.jpg': 'https://images.unsplash.com/photo-1553279768-865429fa0078?w=500&h=500&fit=crop',

  // 商品图片 - 火龙果
  'pitaya1.jpg': 'https://images.unsplash.com/photo-1603833797131-3c0a6e750d7e?w=500&h=500&fit=crop',
  'pitaya2.jpg': 'https://images.unsplash.com/photo-1526318472351-c75fcf070305?w=500&h=500&fit=crop',

  // 商品图片 - 苹果
  'apple1.jpg': 'https://images.unsplash.com/photo-1560806887-1e4cd0b6cbd6?w=500&h=500&fit=crop',
  'apple2.jpg': 'https://images.unsplash.com/photo-1568702846914-96b305d2aaeb?w=500&h=500&fit=crop',

  // 商品图片 - 葡萄
  'grape1.jpg': 'https://images.unsplash.com/photo-1537640538966-79f369143f8f?w=500&h=500&fit=crop',
  'grape2.jpg': 'https://images.unsplash.com/photo-1599819177314-3a855d5ac79b?w=500&h=500&fit=crop',

  // 分类图标
  'tropical.png': 'https://images.unsplash.com/photo-1587132137056-bfbf0166836e?w=300&h=300&fit=crop',
  'temperate.png': 'https://images.unsplash.com/photo-1560806887-1e4cd0b6cbd6?w=300&h=300&fit=crop',
  'plateau.png': 'https://images.unsplash.com/photo-1589217157232-464b505b197f?w=300&h=300&fit=crop',
  'imported.png': 'https://images.unsplash.com/photo-1611080626919-7cf5a9dbab5b?w=300&h=300&fit=crop',
  'nuts.png': 'https://images.unsplash.com/photo-1599599810769-bcde5a160d32?w=300&h=300&fit=crop'
};

/**
 * 获取占位图URL
 * @param {string} type - 图片类型: banner, product, avatar, category
 * @returns {string} 占位图URL
 */
function getPlaceholderImage(type = 'product') {
  return PLACEHOLDER_IMAGES[type] || PLACEHOLDER_IMAGES.product;
}

/**
 * 转换示例URL为真实图片URL
 * @param {string} url - 原始URL
 * @param {string} fallbackType - 占位图类型
 * @returns {string} 真实图片URL
 */
function convertImageUrl(url, fallbackType = 'product') {
  if (!url) {
    return getPlaceholderImage(fallbackType);
  }

  // 如果是example.com的URL，尝试替换为真实图片
  if (url.includes('example.com')) {
    const filename = url.split('/').pop();
    if (FRUIT_IMAGES[filename]) {
      return FRUIT_IMAGES[filename];
    }
  }

  // 如果已经是有效URL，直接返回
  return url;
}

/**
 * 图片加载错误处理
 * @param {Event} e - 事件对象
 * @param {string} fallbackType - 占位图类型
 */
function handleImageError(e, fallbackType = 'product') {
  console.warn('图片加载失败:', e.detail);
  // 返回占位图URL，让调用方设置
  return getPlaceholderImage(fallbackType);
}

/**
 * 批量转换图片URL数组
 * @param {string[]} urls - 图片URL数组
 * @param {string} fallbackType - 占位图类型
 * @returns {string[]} 转换后的URL数组
 */
function convertImageUrls(urls, fallbackType = 'product') {
  if (!Array.isArray(urls)) {
    return [];
  }
  return urls.map(url => convertImageUrl(url, fallbackType));
}

module.exports = {
  getPlaceholderImage,
  convertImageUrl,
  convertImageUrls,
  handleImageError,
  PLACEHOLDER_IMAGES,
  FRUIT_IMAGES
};
