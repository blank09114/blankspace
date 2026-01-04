// 닉네임 폼 토글
function nameToggle(btn)
{
    const userInfo = btn.closest(".userInfo");
    const nameText = userInfo.querySelectorAll(".title3Text")[1];
    const form = userInfo.querySelector('form[name="nameForm"]');
    const input = form.querySelector('input[name="name"]');

    const isEditing = form.style.display === "flex";

    if (isEditing)
    {
        // 보기 모드로 전환
        form.style.display = "none";
        nameText.style.display = "block";
        btn.textContent = "변경";
    }
    else
    {
        // 수정 모드로 전환
        input.value = nameText.textContent.trim();
        nameText.style.display = "none";
        form.style.display = "flex";
        btn.textContent = "취소";
    }
}

// 닉네임 변경
async function editName(btn)
{
    const form = btn.closest('form[name="nameForm"]');
    const userInfo = btn.closest('.userInfo');
    const input = form ? form.querySelector('input[name="name"]') : null;

    if (!input || !userInfo)
    {
        console.error('닉네임 폼 요소를 찾지 못함', { btn, form, userInfo, input });
        showToast("요청 처리 중 문제가 발생했습니다.");
        return;
    }

    const newName = input.value.trim();
    if (!newName) { showToast("닉네임을 입력해주세요."); return; }

    const targetUserId = document.body.dataset.userId;
    if (!targetUserId) { showToast("요청 처리 중 문제가 발생했습니다."); return; }

    const data = await fetchJson(`/api/user/${encodeURIComponent(targetUserId)}/name`,
    {
        method: "PATCH",
        body: JSON.stringify({ userName: newName })
    },
    {
        defaultErrorMessage: "닉네임 변경에 실패했습니다.",
        toastOnSuccess: "닉네임이 변경되었습니다."
    });

    if (!data) return;

    // 화면 갱신
    const nameText = userInfo.querySelectorAll(".title3Text")[1];
    nameText.textContent = data.userName;
    form.style.display = "none";
    nameText.style.display = "block";

    const toggleBtn = userInfo.querySelector('button.btn[onclick^="nameToggle"]');
    if (toggleBtn) toggleBtn.textContent = "변경";
}

// 차단
async function toggleBlock(btn)
{
    const targetUserId = document.body.dataset.userId;
    if (!targetUserId) { showToast("요청 처리 중 문제가 발생했습니다."); return; }

    const isUnblock = btn.textContent.includes("해제");
    let body = null;

    if (!isUnblock)
    {
        const reason = prompt("차단 사유를 입력해주세요. (20자 이내)");
        if (reason === null) return;
        body = { reason: reason.trim() };
    }

    const data = await fetchJson
    (
        `/api/user/${encodeURIComponent(targetUserId)}/block`,
        { method: "PATCH", body: body ? JSON.stringify(body) : null },
        {
            defaultErrorMessage: "처리에 실패했습니다.",
            toastOnSuccess: isUnblock ? "차단이 해제되었습니다." : "사용자를 차단했습니다."
        }
    );

    if (!data) return;
    location.reload();
}

// 로그인 기록 불러오기
async function loadLoginLogs(page = 0)
{
    const list = document.getElementById("loginLogList");
    if (!list) return;

    const userId = document.body.dataset.userId;
    if (!userId) return;

    const data = await fetchJson(
        `/api/user/${encodeURIComponent(userId)}/login-logs?page=${page}`,
        { method: "GET" },
        { defaultErrorMessage: "로그인 기록을 불러오지 못했습니다." }
    );

    if (!data) return;

    renderLoginLogs(data.content);
    const pagination = list.closest(".activityMenu").querySelector("[data-pagination]");

    renderPagination( pagination, data.number, data.totalPages, loadLoginLogs);
}

// 렌더링
function renderLoginLogs(logs)
{
    const list = document.getElementById("loginLogList");
    if (!list) return;

    list.querySelectorAll(".activity:not(.head)").forEach(e => e.remove());

    if (logs.length === 0)
    {
        const empty = document.createElement("div");
        empty.className = "activity flex flex-column align-center";
        empty.innerHTML = `<span class="text3 black2">로그인 기록이 없습니다.</span>`;
        list.appendChild(empty);
        return;
    }

    logs.forEach(log =>
    {
        const item = document.createElement("div");
        item.className = "activity flex flex-column align-center";

        const statusText = log.logoutDate? `${formatDate(log.logoutDate)} 로그아웃됨`: "세션 유지 중";

        item.innerHTML =
        `
            <span class="text3 black2">${statusText}</span>
            <div class="activityBottom flex align-center">
                <span class="text1">${log.loginIp} (${log.loginRegion})</span>
                <span class="text2 date">${formatDate(log.loginDate)}</span>
            </div>
        `;

        list.appendChild(item);
    });
}

// 이벤트 리스너
window.addEventListener("load", () =>
{
    // 로그인 기록 불러오기
    if (document.getElementById("loginLogList")) { loadLoginLogs(0); }
});