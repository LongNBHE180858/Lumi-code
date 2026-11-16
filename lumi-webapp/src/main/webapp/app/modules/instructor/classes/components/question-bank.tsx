import React, { useEffect, useState } from 'react';
import {
  addBankQuestion,
  createQuestionBank,
  downloadBankTemplate,
  getBankQuestions,
  getBankTopics,
  getQuestionBanks,
  importBankExcel,
  type BankSummary,
  type ImportError,
  type QuestionItem,
  type TopicSummary,
} from 'app/shared/util/question-bank-api';

interface Props {
  classId?: number;
}

const difficultyLabels: Record<number, string> = {
  1: '1 - Nhận biết',
  2: '2 - Thông hiểu',
  3: '3 - Vận dụng',
  4: '4 - Vận dụng cao',
  5: '5 - Nâng cao',
};

export const QuestionBankView: React.FC<Props> = ({ classId }) => {
  const [banks, setBanks] = useState<BankSummary[]>([]);
  const [selectedBankId, setSelectedBankId] = useState<number | null>(null);
  const [questions, setQuestions] = useState<QuestionItem[]>([]);
  const [topics, setTopics] = useState<TopicSummary[]>([]);
  const [selectedTopic, setSelectedTopic] = useState<string>('ALL');
  const [selectedDifficulty, setSelectedDifficulty] = useState<number | undefined>(undefined);
  const [loading, setLoading] = useState<boolean>(true);
  const [error, setError] = useState<string | null>(null);
  const [notice, setNotice] = useState<string | null>(null);

  // Modal tạo ngân hàng
  const [showCreateBankModal, setShowCreateBankModal] = useState<boolean>(false);
  const [newBankName, setNewBankName] = useState<string>('');
  const [newBankForClassOnly, setNewBankForClassOnly] = useState<boolean>(true);

  // Modal thêm câu hỏi thủ công
  const [showAddQuestionModal, setShowAddQuestionModal] = useState<boolean>(false);
  const [newContent, setNewContent] = useState<string>('');
  const [newTopic, setNewTopic] = useState<string>('');
  const [newDifficulty, setNewDifficulty] = useState<number>(1);
  const [newExplanation, setNewExplanation] = useState<string>('');
  const [newOptions, setNewOptions] = useState<string[]>(['', '', '', '']);
  const [newCorrectIndex, setNewCorrectIndex] = useState<number>(0);

  // Import Excel state
  const [showImportModal, setShowImportModal] = useState<boolean>(false);
  const [selectedFile, setSelectedFile] = useState<File | null>(null);
  const [importing, setImporting] = useState<boolean>(false);
  const [importErrors, setImportErrors] = useState<ImportError[]>([]);

  // Tải danh sách ngân hàng
  const loadBanks = async (targetSelectId?: number) => {
    try {
      setLoading(true);
      setError(null);
      const list = await getQuestionBanks(classId);
      setBanks(list);
      if (list.length > 0) {
        const nextId = targetSelectId || (selectedBankId && list.some(b => b.id === selectedBankId) ? selectedBankId : list[0].id);
        setSelectedBankId(nextId);
      } else {
        setSelectedBankId(null);
        setQuestions([]);
        setTopics([]);
      }
    } catch (err: any) {
      setError(err?.message || 'Không thể tải danh sách ngân hàng câu hỏi.');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    void loadBanks();
  }, [classId]);

  // Tải câu hỏi và chủ đề khi chọn ngân hàng
  const loadBankContent = async (bankId: number) => {
    try {
      setError(null);
      const [qList, tList] = await Promise.all([getBankQuestions(bankId, selectedTopic, selectedDifficulty), getBankTopics(bankId)]);
      setQuestions(qList);
      setTopics(tList);
    } catch (err: any) {
      setError(err?.message || 'Không thể tải câu hỏi từ ngân hàng.');
    }
  };

  useEffect(() => {
    if (selectedBankId) {
      void loadBankContent(selectedBankId);
    }
  }, [selectedBankId, selectedTopic, selectedDifficulty]);

  // Xử lý tạo ngân hàng
  const handleCreateBank = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!newBankName.trim()) return;
    try {
      setError(null);
      const created = await createQuestionBank({
        name: newBankName.trim(),
        classId: classId && newBankForClassOnly ? classId : null,
      });
      setShowCreateBankModal(false);
      setNewBankName('');
      setNotice(`Đã tạo ngân hàng "${created.name}".`);
      await loadBanks(created.id);
    } catch (err: any) {
      setError(err?.message || 'Không thể tạo ngân hàng câu hỏi.');
    }
  };

  // Xử lý thêm câu hỏi thủ công
  const handleAddQuestion = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!selectedBankId) return;
    const cleanOptions = newOptions.map(o => o.trim()).filter(Boolean);
    if (cleanOptions.length < 2) {
      setError('Vui lòng nhập ít nhất 2 lựa chọn.');
      return;
    }
    if (newCorrectIndex >= cleanOptions.length) {
      setError('Đáp án đúng không hợp lệ.');
      return;
    }
    try {
      setError(null);
      await addBankQuestion(selectedBankId, {
        content: newContent.trim(),
        difficulty: newDifficulty,
        topic: newTopic.trim() || undefined,
        explanation: newExplanation.trim() || undefined,
        options: cleanOptions,
        correctIndex: newCorrectIndex,
      });
      setShowAddQuestionModal(false);
      setNewContent('');
      setNewTopic('');
      setNewExplanation('');
      setNewOptions(['', '', '', '']);
      setNewCorrectIndex(0);
      setNotice('Đã thêm câu hỏi vào ngân hàng.');
      await loadBankContent(selectedBankId);
      await loadBanks(selectedBankId);
    } catch (err: any) {
      setError(err?.message || 'Không thể thêm câu hỏi.');
    }
  };

  // Xử lý import Excel
  const handleImportExcel = async () => {
    if (!selectedBankId || !selectedFile) return;
    try {
      setImporting(true);
      setError(null);
      setImportErrors([]);
      const res = await importBankExcel(selectedBankId, selectedFile);
      if (res.errors && res.errors.length > 0) {
        setImportErrors(res.errors);
      } else {
        setShowImportModal(false);
        setSelectedFile(null);
        setNotice(`Đã nhập thành công ${res.importedCount} câu hỏi vào ngân hàng!`);
        await loadBankContent(selectedBankId);
        await loadBanks(selectedBankId);
      }
    } catch (err: any) {
      setError(err?.response?.data?.message || err?.message || 'Lỗi khi nhập file Excel.');
    } finally {
      setImporting(false);
    }
  };

  return (
    <section className="classroom-panel question-bank-container">
      <div className="classroom-header" style={{ marginBottom: '1.25rem' }}>
        <div>
          <h2>Ngân hàng câu hỏi</h2>
          <p style={{ color: '#64748b', fontSize: '0.9rem' }}>
            Quản lý kho câu hỏi trắc nghiệm, nhập từ Excel hoặc tự tạo câu hỏi để phục vụ lập ma trận đề.
          </p>
        </div>
        <div style={{ display: 'flex', gap: '0.6rem', flexWrap: 'wrap', alignItems: 'center' }}>
          <button
            type="button"
            className="btn-excel-download"
            onClick={() => void downloadBankTemplate()}
            title="Tải file mẫu Excel chuẩn để điền câu hỏi"
          >
            ⬇ Tải mẫu Excel (.xlsx)
          </button>
          <button
            type="button"
            className="btn-excel-upload"
            onClick={() => {
              setImportErrors([]);
              setSelectedFile(null);
              setShowImportModal(true);
            }}
            title="Tải file Excel câu hỏi lên ngân hàng"
          >
            ⬆ Đẩy câu hỏi lên (Excel)
          </button>
          <button type="button" className="btn-pill-primary" onClick={() => setShowCreateBankModal(true)}>
            + Tạo ngân hàng mới
          </button>
        </div>
      </div>

      {error && (
        <div className="classroom-error" role="alert" style={{ marginBottom: '1rem' }}>
          {error}
        </div>
      )}
      {notice && (
        <div className="classroom-notice" role="status" style={{ marginBottom: '1rem' }}>
          {notice}
        </div>
      )}

      {/* Thanh chọn Ngân hàng */}
      <div style={{ background: '#f8fafc', padding: '1rem', borderRadius: '10px', marginBottom: '1.25rem', border: '1px solid #e2e8f0' }}>
        <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', flexWrap: 'wrap', gap: '1rem' }}>
          <div style={{ display: 'flex', alignItems: 'center', gap: '0.75rem', flex: 1, minWidth: '260px' }}>
            <label style={{ fontWeight: 600, color: '#334155', whiteSpace: 'nowrap' }}>Chọn ngân hàng:</label>
            <select
              style={{ padding: '0.5rem 0.75rem', borderRadius: '6px', border: '1px solid #cbd5e1', flex: 1 }}
              value={selectedBankId ?? ''}
              onChange={e => setSelectedBankId(e.target.value ? Number(e.target.value) : null)}
            >
              {banks.length === 0 && <option value="">Chưa có ngân hàng nào</option>}
              {banks.map(b => (
                <option key={b.id} value={b.id}>
                  {b.name} ({b.questionCount} câu) — {b.classId ? `Lớp: ${b.className}` : 'Dùng chung'}
                </option>
              ))}
            </select>
          </div>
          {selectedBankId && (
            <div style={{ display: 'flex', gap: '0.5rem' }}>
              <button
                type="button"
                className="btn-excel-upload"
                onClick={() => {
                  setImportErrors([]);
                  setSelectedFile(null);
                  setShowImportModal(true);
                }}
              >
                ⬆ Đẩy câu hỏi lên (Excel)
              </button>
              <button type="button" className="btn-pill-primary" onClick={() => setShowAddQuestionModal(true)}>
                + Thêm câu hỏi thủ công
              </button>
            </div>
          )}
        </div>
      </div>

      {selectedBankId ? (
        <>
          {/* Bộ lọc theo Chủ đề và Độ khó */}
          <div style={{ display: 'flex', gap: '1rem', flexWrap: 'wrap', marginBottom: '1rem', alignItems: 'center' }}>
            <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
              <span style={{ fontSize: '0.85rem', color: '#64748b', fontWeight: 600 }}>Chủ đề:</span>
              <select
                style={{ padding: '0.35rem 0.6rem', borderRadius: '6px', border: '1px solid #cbd5e1', fontSize: '0.85rem' }}
                value={selectedTopic}
                onChange={e => setSelectedTopic(e.target.value)}
              >
                <option value="ALL">Tất cả chủ đề ({topics.reduce((acc, t) => acc + t.totalQuestions, 0)} câu)</option>
                {topics.map(t => (
                  <option key={t.topic} value={t.topic}>
                    {t.topic} ({t.totalQuestions} câu)
                  </option>
                ))}
              </select>
            </div>

            <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
              <span style={{ fontSize: '0.85rem', color: '#64748b', fontWeight: 600 }}>Độ khó:</span>
              <select
                style={{ padding: '0.35rem 0.6rem', borderRadius: '6px', border: '1px solid #cbd5e1', fontSize: '0.85rem' }}
                value={selectedDifficulty ?? ''}
                onChange={e => setSelectedDifficulty(e.target.value ? Number(e.target.value) : undefined)}
              >
                <option value="">Tất cả độ khó</option>
                <option value="1">1 - Nhận biết</option>
                <option value="2">2 - Thông hiểu</option>
                <option value="3">3 - Vận dụng</option>
                <option value="4">4 - Vận dụng cao</option>
                <option value="5">5 - Nâng cao</option>
              </select>
            </div>

            <div style={{ marginLeft: 'auto', fontSize: '0.85rem', color: '#64748b' }}>
              Hiển thị: <strong>{questions.length}</strong> câu hỏi
            </div>
          </div>

          {/* Danh sách câu hỏi */}
          {questions.length === 0 ? (
            <div style={{ textAlign: 'center', padding: '2.5rem', background: '#f8fafc', borderRadius: '8px', color: '#64748b' }}>
              <p style={{ margin: 0, fontWeight: 500 }}>Chưa có câu hỏi nào thỏa mãn bộ lọc trong ngân hàng này.</p>
              <p style={{ margin: '0.5rem 0 0 0', fontSize: '0.85rem' }}>
                Hãy nhấn &quot;+ Thêm câu hỏi&quot; hoặc &quot;📥 Nhập từ Excel&quot; để bổ sung câu hỏi.
              </p>
            </div>
          ) : (
            <div style={{ display: 'flex', flexDirection: 'column', gap: '0.85rem' }}>
              {questions.map((q, idx) => (
                <div
                  key={q.id}
                  style={{
                    border: '1px solid #e2e8f0',
                    borderRadius: '8px',
                    padding: '1rem',
                    background: '#fff',
                    boxShadow: '0 1px 2px rgba(0,0,0,0.03)',
                  }}
                >
                  <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', marginBottom: '0.5rem' }}>
                    <div style={{ display: 'flex', gap: '0.5rem', alignItems: 'center' }}>
                      <span style={{ fontWeight: 700, color: '#0284c7' }}>Câu {idx + 1}.</span>
                      <span
                        style={{
                          background: '#e0f2fe',
                          color: '#0369a1',
                          padding: '0.15rem 0.5rem',
                          borderRadius: '4px',
                          fontSize: '0.75rem',
                          fontWeight: 600,
                        }}
                      >
                        {q.topic}
                      </span>
                      <span
                        style={{
                          background: '#f1f5f9',
                          color: '#475569',
                          padding: '0.15rem 0.5rem',
                          borderRadius: '4px',
                          fontSize: '0.75rem',
                        }}
                      >
                        {difficultyLabels[q.difficulty] || `Độ khó ${q.difficulty}`}
                      </span>
                    </div>
                  </div>

                  <p style={{ margin: '0.4rem 0 0.8rem 0', fontWeight: 500, color: '#1e293b', whiteSpace: 'pre-wrap' }}>{q.content}</p>

                  <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(220px, 1fr))', gap: '0.5rem' }}>
                    {q.options.map((opt, optIdx) => {
                      const letter = String.fromCharCode(65 + optIdx);
                      return (
                        <div
                          key={opt.id}
                          style={{
                            padding: '0.5rem 0.75rem',
                            borderRadius: '6px',
                            border: opt.isCorrect ? '1.5px solid #16a34a' : '1px solid #e2e8f0',
                            background: opt.isCorrect ? '#f0fdf4' : '#fafafa',
                            color: opt.isCorrect ? '#15803d' : '#334155',
                            display: 'flex',
                            alignItems: 'center',
                            gap: '0.5rem',
                            fontSize: '0.9rem',
                          }}
                        >
                          <strong style={{ minWidth: '1.25rem' }}>{letter}.</strong>
                          <span style={{ flex: 1 }}>{opt.content}</span>
                          {opt.isCorrect && <span style={{ fontWeight: 700 }}>✓</span>}
                        </div>
                      );
                    })}
                  </div>

                  {q.explanation && (
                    <div
                      style={{
                        marginTop: '0.6rem',
                        padding: '0.5rem 0.75rem',
                        background: '#f8fafc',
                        borderRadius: '4px',
                        fontSize: '0.85rem',
                        color: '#64748b',
                      }}
                    >
                      💡 <em>Giải thích:</em> {q.explanation}
                    </div>
                  )}
                </div>
              ))}
            </div>
          )}
        </>
      ) : (
        <div style={{ textAlign: 'center', padding: '3rem', color: '#64748b' }}>
          <p>Chưa có ngân hàng câu hỏi nào. Hãy tạo ngân hàng đầu tiên để bắt đầu!</p>
        </div>
      )}

      {/* Modal Tạo ngân hàng */}
      {showCreateBankModal && (
        <div
          style={{
            position: 'fixed',
            inset: 0,
            background: 'rgba(0,0,0,0.5)',
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center',
            zIndex: 1000,
          }}
        >
          <div style={{ background: '#fff', borderRadius: '10px', padding: '1.5rem', width: '420px', maxWidth: '90%' }}>
            <h3 style={{ marginTop: 0 }}>Tạo ngân hàng câu hỏi mới</h3>
            <form onSubmit={e => void handleCreateBank(e)}>
              <div style={{ marginBottom: '1rem' }}>
                <label style={{ display: 'block', fontWeight: 600, marginBottom: '0.35rem' }}>Tên ngân hàng (*):</label>
                <input
                  type="text"
                  required
                  placeholder="VD: Ngân hàng Toán Đại số 7"
                  style={{ width: '100%', padding: '0.5rem', borderRadius: '6px', border: '1px solid #cbd5e1' }}
                  value={newBankName}
                  onChange={e => setNewBankName(e.target.value)}
                />
              </div>
              {classId && (
                <div style={{ marginBottom: '1rem' }}>
                  <label style={{ display: 'flex', alignItems: 'center', gap: '0.5rem', cursor: 'pointer' }}>
                    <input type="checkbox" checked={newBankForClassOnly} onChange={e => setNewBankForClassOnly(e.target.checked)} />
                    <span>Chỉ dùng riêng cho lớp học hiện tại (không dùng chung)</span>
                  </label>
                </div>
              )}
              <div style={{ display: 'flex', justifyContent: 'flex-end', gap: '0.5rem' }}>
                <button type="button" className="classroom-secondary-action" onClick={() => setShowCreateBankModal(false)}>
                  Hủy
                </button>
                <button type="submit" className="btn-pill-primary">
                  Tạo ngân hàng
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

      {/* Modal Thêm câu hỏi */}
      {showAddQuestionModal && (
        <div
          style={{
            position: 'fixed',
            inset: 0,
            background: 'rgba(0,0,0,0.5)',
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center',
            zIndex: 1000,
          }}
        >
          <div
            style={{
              background: '#fff',
              borderRadius: '10px',
              padding: '1.5rem',
              width: '560px',
              maxWidth: '92%',
              maxHeight: '90vh',
              overflowY: 'auto',
            }}
          >
            <h3 style={{ marginTop: 0 }}>Thêm câu hỏi mới vào ngân hàng</h3>
            <form onSubmit={e => void handleAddQuestion(e)}>
              <div style={{ marginBottom: '0.75rem' }}>
                <label style={{ display: 'block', fontWeight: 600, marginBottom: '0.25rem' }}>Nội dung câu hỏi (*):</label>
                <textarea
                  required
                  rows={3}
                  style={{ width: '100%', padding: '0.5rem', borderRadius: '6px', border: '1px solid #cbd5e1' }}
                  value={newContent}
                  onChange={e => setNewContent(e.target.value)}
                />
              </div>

              <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '0.75rem', marginBottom: '0.75rem' }}>
                <div>
                  <label style={{ display: 'block', fontWeight: 600, marginBottom: '0.25rem' }}>Chủ đề:</label>
                  <input
                    type="text"
                    placeholder="VD: Phân số, Hình học..."
                    style={{ width: '100%', padding: '0.45rem', borderRadius: '6px', border: '1px solid #cbd5e1' }}
                    value={newTopic}
                    onChange={e => setNewTopic(e.target.value)}
                  />
                </div>
                <div>
                  <label style={{ display: 'block', fontWeight: 600, marginBottom: '0.25rem' }}>Độ khó (*):</label>
                  <select
                    style={{ width: '100%', padding: '0.45rem', borderRadius: '6px', border: '1px solid #cbd5e1' }}
                    value={newDifficulty}
                    onChange={e => setNewDifficulty(Number(e.target.value))}
                  >
                    <option value={1}>1 - Nhận biết</option>
                    <option value={2}>2 - Thông hiểu</option>
                    <option value={3}>3 - Vận dụng</option>
                    <option value={4}>4 - Vận dụng cao</option>
                    <option value={5}>5 - Nâng cao</option>
                  </select>
                </div>
              </div>

              <div style={{ marginBottom: '0.75rem' }}>
                <label style={{ display: 'block', fontWeight: 600, marginBottom: '0.25rem' }}>Các lựa chọn & Đáp án đúng (*):</label>
                <small style={{ color: '#64748b', display: 'block', marginBottom: '0.4rem' }}>
                  Tích chọn vào nút tròn để chọn đáp án đúng.
                </small>
                {newOptions.map((opt, oIdx) => (
                  <div key={oIdx} style={{ display: 'flex', alignItems: 'center', gap: '0.5rem', marginBottom: '0.4rem' }}>
                    <input
                      type="radio"
                      name="correctOptionRadio"
                      checked={newCorrectIndex === oIdx}
                      onChange={() => setNewCorrectIndex(oIdx)}
                      title="Chọn đáp án đúng"
                    />
                    <strong style={{ minWidth: '1.25rem' }}>{String.fromCharCode(65 + oIdx)}.</strong>
                    <input
                      type="text"
                      placeholder={`Lựa chọn ${String.fromCharCode(65 + oIdx)}`}
                      style={{ flex: 1, padding: '0.4rem', borderRadius: '4px', border: '1px solid #cbd5e1' }}
                      value={opt}
                      onChange={e => {
                        const next = [...newOptions];
                        next[oIdx] = e.target.value;
                        setNewOptions(next);
                      }}
                      required={oIdx < 2}
                    />
                  </div>
                ))}
              </div>

              <div style={{ marginBottom: '1rem' }}>
                <label style={{ display: 'block', fontWeight: 600, marginBottom: '0.25rem' }}>Giải thích chi tiết (tùy chọn):</label>
                <textarea
                  rows={2}
                  style={{ width: '100%', padding: '0.45rem', borderRadius: '6px', border: '1px solid #cbd5e1' }}
                  value={newExplanation}
                  onChange={e => setNewExplanation(e.target.value)}
                />
              </div>

              <div style={{ display: 'flex', justifyContent: 'flex-end', gap: '0.5rem' }}>
                <button type="button" className="classroom-secondary-action" onClick={() => setShowAddQuestionModal(false)}>
                  Hủy
                </button>
                <button type="submit" className="btn-pill-primary">
                  Lưu câu hỏi
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

      {/* Modal Nhập từ Excel */}
      {showImportModal && (
        <div
          style={{
            position: 'fixed',
            inset: 0,
            background: 'rgba(0,0,0,0.5)',
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center',
            zIndex: 1000,
          }}
        >
          <div
            style={{
              background: '#fff',
              borderRadius: '12px',
              padding: '1.75rem',
              width: '560px',
              maxWidth: '94%',
              maxHeight: '90vh',
              overflowY: 'auto',
              boxShadow: '0 10px 25px rgba(0,0,0,0.2)',
            }}
          >
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', marginBottom: '0.75rem' }}>
              <div>
                <h3 style={{ margin: 0, fontSize: '1.25rem', color: '#0f172a' }}>Đẩy câu hỏi lên bằng file Excel</h3>
                <p style={{ fontSize: '0.875rem', color: '#64748b', margin: '0.35rem 0 0 0' }}>
                  Sử dụng mẫu template chuẩn (.xlsx) để tải hàng loạt câu hỏi vào ngân hàng.
                </p>
              </div>
              <button
                type="button"
                className="btn-excel-download"
                onClick={() => void downloadBankTemplate()}
                title="Tải template file Excel mẫu"
              >
                ⬇ Tải mẫu (.xlsx)
              </button>
            </div>

            {banks.length > 0 && (
              <div style={{ marginBottom: '1rem' }}>
                <label style={{ display: 'block', fontWeight: 600, color: '#334155', marginBottom: '0.35rem' }}>
                  Ngân hàng tiếp nhận (*):
                </label>
                <select
                  style={{ width: '100%', padding: '0.55rem', borderRadius: '6px', border: '1px solid #cbd5e1' }}
                  value={selectedBankId ?? ''}
                  onChange={e => setSelectedBankId(Number(e.target.value))}
                >
                  {banks.map(b => (
                    <option key={b.id} value={b.id}>
                      {b.name} ({b.questionCount} câu) — {b.classId ? `Lớp: ${b.className}` : 'Dùng chung'}
                    </option>
                  ))}
                </select>
              </div>
            )}

            <div
              style={{
                border: '2px dashed #93c5fd',
                borderRadius: '8px',
                padding: '1.25rem',
                textAlign: 'center',
                background: '#f8fafc',
                marginBottom: '1rem',
              }}
            >
              <div style={{ fontSize: '2rem', marginBottom: '0.25rem' }}>📄</div>
              <p style={{ margin: '0 0 0.5rem 0', fontWeight: 600, color: '#1e293b' }}>
                {selectedFile ? selectedFile.name : 'Chọn file Excel (.xlsx) đã điền câu hỏi'}
              </p>
              <input
                type="file"
                accept=".xlsx,application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
                onChange={e => setSelectedFile(e.target.files?.[0] || null)}
                style={{ fontSize: '0.9rem' }}
              />
            </div>

            {importErrors.length > 0 && (
              <div
                style={{
                  background: '#fef2f2',
                  border: '1px solid #fecaca',
                  borderRadius: '8px',
                  padding: '0.85rem',
                  marginBottom: '1rem',
                }}
              >
                <strong style={{ color: '#b91c1c', display: 'block', marginBottom: '0.35rem' }}>
                  ⚠️ Phát hiện {importErrors.length} lỗi trong file (chưa lưu vào DB):
                </strong>
                <ul
                  style={{
                    margin: 0,
                    paddingLeft: '1.25rem',
                    color: '#991b1b',
                    fontSize: '0.85rem',
                    maxHeight: '180px',
                    overflowY: 'auto',
                  }}
                >
                  {importErrors.map((err, errIdx) => (
                    <li key={errIdx} style={{ marginBottom: '0.25rem' }}>
                      <strong>Dòng {err.row}:</strong> {err.message}
                    </li>
                  ))}
                </ul>
              </div>
            )}

            <div style={{ display: 'flex', justifyContent: 'flex-end', gap: '0.6rem', marginTop: '1.25rem' }}>
              <button
                type="button"
                className="classroom-secondary-action"
                disabled={importing}
                onClick={() => {
                  setShowImportModal(false);
                  setImportErrors([]);
                }}
              >
                Đóng
              </button>
              <button
                type="button"
                className="btn-excel-upload"
                disabled={importing || !selectedFile || !selectedBankId}
                onClick={() => void handleImportExcel()}
              >
                {importing ? 'Đang kiểm tra & nhập...' : '⬆ Xác nhận đẩy câu hỏi lên'}
              </button>
            </div>
          </div>
        </div>
      )}
    </section>
  );
};

export default QuestionBankView;
