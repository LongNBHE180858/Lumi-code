import React from 'react';

interface IErrorBoundaryProps {
  readonly children: React.ReactNode;
}

interface IErrorBoundaryState {
  readonly error: any;
  readonly errorInfo: any;
}

class ErrorBoundary extends React.Component<IErrorBoundaryProps, IErrorBoundaryState> {
  readonly state: IErrorBoundaryState = { error: undefined, errorInfo: undefined };

  componentDidCatch(error, errorInfo) {
    this.setState({
      error,
      errorInfo,
    });
  }

  render() {
    const { error, errorInfo } = this.state;
    if (errorInfo) {
      const errorDetails = DEVELOPMENT ? (
        <details className="preserve-space">
          {error?.toString()}
          <br />
          {errorInfo.componentStack}
        </details>
      ) : undefined;
      const isDynamicImportError =
        error?.message?.includes?.('Failed to fetch dynamically imported module') ||
        error?.toString?.()?.includes?.('Failed to fetch dynamically imported module');

      return (
        <div className="p-4 my-4 bg-light rounded border text-center">
          <h2 className="error text-danger mb-3">An unexpected error has occurred.</h2>
          {isDynamicImportError ? (
            <p className="text-muted">
              Mô-đun vừa được cập nhật hoặc kết nối mạng bị gián đoạn trong giây lát. Vui lòng nhấn nút bên dưới để tải lại trang.
            </p>
          ) : (
            <p className="text-muted">Đã xảy ra lỗi không mong muốn khi hiển thị trang.</p>
          )}
          <button className="btn btn-primary px-4 py-2 my-2" onClick={() => window.location.reload()}>
            Tải lại trang (F5)
          </button>
          {errorDetails}
        </div>
      );
    }
    return this.props.children;
  }
}

export default ErrorBoundary;
