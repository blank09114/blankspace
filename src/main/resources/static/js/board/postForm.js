function subPost()
{
    const form = document.getElementById("postForm");
    if (!form) { console.error("form#postForm not found"); return; }

    const categoryEl = form.querySelector('[name="categoryId"]');
    const titleEl = form.querySelector('[name="postTitle"]');
    const contentEl = form.querySelector('[name="postContent"]');

    if (!checkEl(categoryEl, "카테고리")) return;
    if (!checkEl(titleEl, "제목")) return;

    if (window.RichEditor && typeof window.RichEditor.sync === "function")
    { window.RichEditor.sync(); } else if (window.tinymce) { tinymce.triggerSave(); }

    if (!checkEl(contentEl, "내용")) return;
    HTMLFormElement.prototype.submit.call(form);
}