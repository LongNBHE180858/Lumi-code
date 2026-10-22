import { toast } from 'react-toastify';
import { builderErrorText } from '../course-builder-error';
import React, { useState, useEffect, useRef } from 'react';
import { Row, Col, Card, Form, Button } from 'react-bootstrap';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faArrowRight } from '@fortawesome/free-solid-svg-icons';
import { useAppDispatch, useAppSelector } from 'app/config/store';
import { createDraft, updateBasicInfo, fetchSubjects, fetchGrades } from '../course-builder.reducer';
import LearningObjectives from './learning-objectives';
import CourseMediaIntro from './course-media-intro';
import { isBuilderReadOnly } from '../course-lifecycle';

interface CourseBasicInfoProps {
  onSaved: (courseId: number, goNext: boolean) => void;
  onDirtyChange: (dirty: boolean) => void;
}

export const CourseBasicInfo = ({ onSaved, onDirtyChange }: CourseBasicInfoProps) => {
  const saveInProgressRef = useRef(false);
  const hydratedCourseIdRef = useRef<number | null>(null);
  const [infoDirty, setInfoDirty] = useState(false);
  const [submitting, setSubmitting] = useState(false);

  const dispatch = useAppDispatch();
  const { data: builderData, saving } = useAppSelector(state => state.courseBuilder);
  const course = builderData?.course;
  const readOnly = isBuilderReadOnly(builderData);

  // State các trường của Form
  const [title, setTitle] = useState('');
  const [subjectId, setSubjectId] = useState<number | undefined>();
  const [gradeId, setGradeId] = useState<number | undefined>();
  const [bookSeries, setBookSeries] = useState('Kết nối tri thức với cuộc sống');
  const [shortDesc, setShortDesc] = useState('');
  const [fullDesc, setFullDesc] = useState('');
  const [targetAudience, setTargetAudience] = useState('');
  const [prerequisites, setPrerequisites] = useState('');
  const [objectives, setObjectives] = useState<string[]>([]);

  // Danh sách dropdown lấy từ BE
  const [subjectsList, setSubjectsList] = useState<any[]>([]);
  const [gradesList, setGradesList] = useState<any[]>([]);

  // 1. Tải danh sách môn & lớp khi component mount
  useEffect(() => {
    fetchSubjects()
      .then(setSubjectsList)
      .catch(() => toast.error('Không tải được danh sách môn học.'));
    fetchGrades()
      .then(setGradesList)
      .catch(() => toast.error('Không tải được danh sách lớp.'));
  }, []);

  // 2. Điền dữ liệu từ Redux Store vào Form
  useEffect(() => {
    if (course && (hydratedCourseIdRef.current !== course.id || readOnly) && !saveInProgressRef.current) {
      hydratedCourseIdRef.current = course.id;
      setInfoDirty(false);
      onDirtyChange(false);
      setTitle(course.title || '');
      setSubjectId(course.subject?.id);
      setGradeId(course.grade?.id);
      setBookSeries(course.bookSeries || 'Kết nối tri thức với cuộc sống');
      setShortDesc(course.shortDescription || '');
      setFullDesc(course.description || '');
      setTargetAudience(course.targetAudience || '');
      setPrerequisites(course.prerequisites || '');
      setObjectives(course.learningObjectives || []);
    }
  }, [course, readOnly]);

  const markDirty = () => {
    setInfoDirty(true);
    onDirtyChange(true);
  };

  // 3. Xử lý lưu form và chuyển tab
  const handleSave = async (event: React.FormEvent<HTMLFormElement>) => {
    event.preventDefault(); // Không submit bằng cách reload trang.

    if (saveInProgressRef.current || saving || readOnly) return;

    if (!title.trim()) {
      toast.error('Vui lòng nhập tên khóa học.');
      return;
    }
    // Xác định người dùng bấm nút nào.
    const submitter = (event.nativeEvent as SubmitEvent).submitter as HTMLButtonElement | null;
    const goNext = submitter?.value === 'next';

    // Chụp nội dung form trước khi gọi API.
    const formData = {
      title: title.trim(),
      subjectId: subjectId ?? null,
      gradeId: gradeId ?? null,
      bookSeries,
      shortDescription: shortDesc,
      description: fullDesc,
      targetAudience,
      prerequisites,
      // Không gửi các dòng mục tiêu đang để trống.
      learningObjectives: objectives.map(item => item.trim()).filter(Boolean),
    };

    saveInProgressRef.current = true;
    setSubmitting(true);

    try {
      let currentCourse = course;

      // Chưa có ID: tạo draft trước.
      if (!currentCourse?.id) {
        const created = await dispatch(createDraft(formData.title)).unwrap();
        currentCourse = created.course;
      }
      // Có ID và version rồi mới lưu thông tin.
      const saved = await dispatch(
        updateBasicInfo({
          courseId: currentCourse.id,
          data: {
            ...formData,
            version: currentCourse.version,
          },
        }),
      ).unwrap();
      // Chỉ thông báo thành công/chuyển tab sau khi PUT thành công.
      hydratedCourseIdRef.current = saved.course.id;
      setTitle(saved.course.title);
      setObjectives(saved.course.learningObjectives || []);
      setInfoDirty(false);
      onDirtyChange(false);
      toast.success('Lưu thông tin khóa học thành công.');
      onSaved(saved.course.id, goNext);
    } catch (error) {
      toast.error(builderErrorText(error, 'Không lưu được thông tin. Nội dung đang nhập vẫn được giữ.'));
    } finally {
      saveInProgressRef.current = false;
      setSubmitting(false);
    }
  };

  return (
    <form id="course-basic-info-form" onSubmit={handleSave}>
      <fieldset disabled={saving || submitting || readOnly} className="border-0 p-0 m-0">
        <Row className="g-4">
          {/* CỘT TRÁI: CÁC TRƯỜNG THÔNG TIN VĂN BẢN */}
          <Col lg={8} onChange={markDirty}>
            {/* Mục 1: Thông tin cơ bản */}
            <Card className="p-4 rounded-4 border-0 shadow-sm mb-4">
              <h5 className="fw-bold mb-3">Thông tin cơ bản</h5>
              <Form.Group className="mb-3">
                <Form.Label className="fw-semibold">
                  Tên khóa học <span className="text-danger">*</span>
                </Form.Label>
                <Form.Control
                  type="text"
                  value={title}
                  onChange={e => setTitle(e.target.value)}
                  placeholder="Nhập tên khóa học..."
                  className="py-2 rounded-3"
                />
                {/* <Form.Text className="text-muted small">✓ Tên khóa học hợp lệ và dễ nhận biết.</Form.Text> */}
              </Form.Group>

              {/* Môn học & Lớp */}
              <Row className="g-3 mb-3">
                <Col md={6}>
                  <Form.Group>
                    <Form.Label className="fw-semibold">
                      Môn học <span className="text-danger">*</span>
                    </Form.Label>
                    <Form.Select
                      value={subjectId || ''}
                      onChange={e => setSubjectId(e.target.value ? Number(e.target.value) : undefined)}
                      className="py-2 rounded-3"
                    >
                      <option value="">-- Chọn môn học --</option>
                      {subjectsList.map(s => (
                        <option key={s.id} value={s.id}>
                          {s.name}
                        </option>
                      ))}
                    </Form.Select>
                  </Form.Group>
                </Col>
                <Col md={6}>
                  <Form.Group>
                    <Form.Label className="fw-semibold">
                      Lớp <span className="text-danger">*</span>
                    </Form.Label>
                    <Form.Select
                      value={gradeId || ''}
                      onChange={e => setGradeId(e.target.value ? Number(e.target.value) : undefined)}
                      className="py-2 rounded-3"
                    >
                      <option value="">-- Chọn lớp --</option>
                      {gradesList.map(g => (
                        <option key={g.id} value={g.id}>
                          {g.name}
                        </option>
                      ))}
                    </Form.Select>
                  </Form.Group>
                </Col>
              </Row>

              {/* Bộ sách giáo khoa */}
              <Form.Group>
                <Form.Label className="fw-semibold">Bộ sách giáo khoa</Form.Label>
                <Form.Select value={bookSeries} onChange={e => setBookSeries(e.target.value)} className="py-2 rounded-3">
                  <option value="Kết nối tri thức với cuộc sống">Kết nối tri thức với cuộc sống</option>
                </Form.Select>
                <Form.Text className="text-muted small">Hiện tại hệ thống hỗ trợ bộ sách Kết nối tri thức với cuộc sống.</Form.Text>
              </Form.Group>
            </Card>

            {/* Mục 2: Giới thiệu khóa học */}
            <Card className="p-4 rounded-4 border-0 shadow-sm mb-4">
              <h5 className="fw-bold mb-3">Giới thiệu khóa học</h5>
              <Form.Group className="mb-3">
                <Form.Label className="fw-semibold">Mô tả ngắn</Form.Label>
                <Form.Control
                  as="textarea"
                  rows={2}
                  value={shortDesc}
                  onChange={e => setShortDesc(e.target.value)}
                  placeholder="Tóm tắt ngắn gọn khóa học..."
                  className="rounded-3"
                />
              </Form.Group>

              <Form.Group>
                <Form.Label className="fw-semibold">
                  Mô tả đầy đủ <span className="text-danger">*</span>
                </Form.Label>
                <Form.Control
                  as="textarea"
                  rows={5}
                  value={fullDesc}
                  onChange={e => setFullDesc(e.target.value)}
                  placeholder="Giới thiệu đầy đủ nội dung..."
                  className="rounded-3"
                />
              </Form.Group>
            </Card>

            {/* Mục 3: Đối tượng & điều kiện */}
            <Card className="p-4 rounded-4 border-0 shadow-sm mb-4">
              <h5 className="fw-bold mb-3">Đối tượng & kiến thức cần có</h5>
              <Form.Group className="mb-3">
                <Form.Label className="fw-semibold">
                  Khóa học dành cho ai? <span className="text-danger">*</span>
                </Form.Label>
                <Form.Control
                  as="textarea"
                  rows={2}
                  value={targetAudience}
                  onChange={e => setTargetAudience(e.target.value)}
                  className="rounded-3"
                />
              </Form.Group>

              <Form.Group>
                <Form.Label className="fw-semibold">Điều kiện học trước</Form.Label>
                <Form.Control
                  type="text"
                  value={prerequisites}
                  onChange={e => setPrerequisites(e.target.value)}
                  className="py-2 rounded-3"
                />
                <Form.Text className="text-muted small">Giúp học viên chuẩn bị trước khi bắt đầu.</Form.Text>
              </Form.Group>
            </Card>

            {/* Mục 4: Mục tiêu học tập (Kéo thả, thêm, xóa) */}
            <LearningObjectives
              objectives={objectives}
              onChange={updated => {
                setObjectives(updated);
                markDirty();
              }}
            />
          </Col>

          {/* CỘT PHẢI: TÀI SẢN MEDIA & CHECKLIST SẴN SÀNG */}
          <Col lg={4}>
            <CourseMediaIntro basicInfo={{ title, subjectId, gradeId }} infoDirty={infoDirty} />
          </Col>
        </Row>

        {/* Nút bấm Lưu và Chuyển bước */}
        <div className="d-flex justify-content-end mt-4">
          <Button
            variant="primary"
            className="rounded-pill px-4 py-2 fw-semibold"
            type="submit"
            name="intent"
            value="next"
            disabled={saving || submitting}
          >
            {saving ? 'Đang lưu...' : 'Lưu và Tiếp tục'} <FontAwesomeIcon icon={faArrowRight} className="ms-2" />
          </Button>
        </div>
      </fieldset>
    </form>
  );
};

export default CourseBasicInfo;
